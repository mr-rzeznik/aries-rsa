/*
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.aries.rsa.provider.fastbin;

import java.util.HashMap;
import java.util.Map;
import org.apache.aries.rsa.provider.fastbin.api.Intent;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Devs-PS
 */
public class IntentListener implements ServiceListener {

    protected static final Logger LOG = LoggerFactory.getLogger(IntentListener.class);
    public static final String FILTER = "(objectClass=" + Intent.class.getName() + ")";

    private final BundleContext bundleContext;
    private final FastBinProvider fastBinProvider;
    private final Map<ServiceReference<Intent>, Intent> serviceRefsSet;

    public IntentListener(BundleContext bundleContex, FastBinProvider fastBinProvider) throws InvalidSyntaxException {
        this.bundleContext = bundleContex;
        this.fastBinProvider = fastBinProvider;
        this.serviceRefsSet = new HashMap<>();
        for (ServiceReference<Intent> serviceRef: this.bundleContext.getServiceReferences(Intent.class, null)) {
            register(serviceRef);
        }
        this.bundleContext.addServiceListener(this, FILTER);
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        if (event.getType() == ServiceEvent.REGISTERED) {
            register((ServiceReference<Intent>)event.getServiceReference());
        } else if (event.getType() == ServiceEvent.UNREGISTERING) {
            unregister((ServiceReference<Intent>)event.getServiceReference());
        }
    }
    
    private synchronized void register(ServiceReference<Intent> serviceRef) {
        String intentName = (String) serviceRef.getProperty(Intent.INTENT_NAME);
        if(intentName == null || intentName.isEmpty()) {
            LOG.info("Ignoring intent[{}] with null/empty name.", serviceRef.getClass());
        }
        if (serviceRefsSet.containsKey(serviceRef)) {
            unregister(serviceRef);
        }
        Intent service = bundleContext.getService(serviceRef);
        if (service == null) return; //if not available
        serviceRefsSet.put(serviceRef, service);
        fastBinProvider.registerIntent(intentName, service);
        LOG.debug("Registered Intent, name[{}], cls[{}].", intentName, service.getClass().getName());
    }
    
    private synchronized void unregister(ServiceReference<Intent> serviceRef) {
        if (!serviceRefsSet.containsKey(serviceRef)) return;
        String intentName = (String) serviceRef.getProperty(Intent.INTENT_NAME);
        fastBinProvider.unregisterIntent(intentName);
        bundleContext.ungetService(serviceRef);
        LOG.debug("Registered Intent, name[{}]", intentName);
    }
    
    public synchronized void close() {
        try {
            bundleContext.removeServiceListener(this);
        } finally {
            while (!serviceRefsSet.isEmpty()) {
                unregister(serviceRefsSet.keySet().iterator().next());
            }
        }
    }
  
}
