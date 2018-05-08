/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.rsa.provider.tcp.ser;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicObjectInputStream extends ObjectInputStream {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private ClassLoader loader;

    public BasicObjectInputStream(InputStream in, ClassLoader loader) throws IOException {
        super(in);
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                enableResolveObject(true);
                return null;
            }
        });
        this.loader = loader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        try {
            String className = desc.getName();
            // Must use Class.forName instead of loader.loadClass to handle cases like array of user classes
            return Class.forName(className, false, loader);
        } catch (ClassNotFoundException e) {
            log.debug("Error loading class using classloader of user bundle. Trying our own ClassLoader now", e);
            return super.resolveClass(desc);
        }
    }
    
    @Override
    protected Object resolveObject(Object obj) throws IOException {
        if (obj instanceof VersionMarker) {
            VersionMarker verionMarker = (VersionMarker)obj;
            return Version.parseVersion(verionMarker.getVersion());
        } else if (obj instanceof DTOMarker) {
            DTOMarker dtoMarker = (DTOMarker)obj;
            return dtoMarker.getDTO(loader);
        } else {
            return super.resolveObject(obj);
        }
    }
}
