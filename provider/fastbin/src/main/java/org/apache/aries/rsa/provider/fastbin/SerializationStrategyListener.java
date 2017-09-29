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
import org.apache.aries.rsa.provider.fastbin.api.SerializationStrategy;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mr Rzeznik
 */
public class SerializationStrategyListener implements ServiceListener {
  
    protected final Logger logger = LoggerFactory.getLogger(getClass());  
  
    public static final String FILTER = "(objectClass=" + SerializationStrategy.class.getName() + ")";
    
    private final BundleContext bundleContext;
    private final FastBinProvider fastBinProvider;
    private final Map<ServiceReference<SerializationStrategy>, SerializationStrategy> serviceRefsSet;
    
    public SerializationStrategyListener(BundleContext bundleContex, FastBinProvider fastBinProvider) throws InvalidSyntaxException {
        this.bundleContext = bundleContex;
        this.fastBinProvider = fastBinProvider;
        this.serviceRefsSet = new HashMap<>();
        for (ServiceReference<SerializationStrategy> serviceRef: this.bundleContext.getServiceReferences(SerializationStrategy.class, null)) {
            register(serviceRef);
        }
        this.bundleContext.addServiceListener(this, FILTER);
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        if (event.getType() == ServiceEvent.REGISTERED) {
            register((ServiceReference<SerializationStrategy>)event.getServiceReference());
        } else if (event.getType() == ServiceEvent.UNREGISTERING) {
            unregister((ServiceReference<SerializationStrategy>)event.getServiceReference());
        }
    }
    
    private synchronized void register(ServiceReference<SerializationStrategy> serviceRef) {
        if (serviceRefsSet.containsKey(serviceRef)) {
            unregister(serviceRef);
        }
        SerializationStrategy service = bundleContext.getService(serviceRef);
        if (service == null) return; //if not available
        serviceRefsSet.put(serviceRef, service);
        fastBinProvider.registerSerializationStrategy(service);
        logger.debug("Registered SerializationStrategy (name: {}, class: {})", service.name(), service.getClass().getName());
    }
    
    private synchronized void unregister(ServiceReference<SerializationStrategy> serviceRef) {
        if (!serviceRefsSet.containsKey(serviceRef)) return;
        SerializationStrategy service = serviceRefsSet.remove(serviceRef);
        fastBinProvider.unregisterSerializationStrategy(service);
        bundleContext.ungetService(serviceRef);
        logger.debug("Unregistered SerializationStrategy (name: {}, class: {})", service.name(), service.getClass().getName());
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
