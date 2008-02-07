/*
 * Copyright (C) 2005-2008 Les Hazlewood
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the
 *
 * Free Software Foundation, Inc.
 * 59 Temple Place, Suite 330
 * Boston, MA 02111-1307
 * USA
 *
 * Or, you may view it online at
 * http://www.opensource.org/licenses/lgpl-license.php
 */
package org.jsecurity.authc.credential.support;

import org.jsecurity.authc.credential.CredentialMatcher;
import org.jsecurity.codec.Base64;
import org.jsecurity.codec.EncodingSupport;
import org.jsecurity.codec.Hex;
import org.jsecurity.crypto.Hash;
import org.jsecurity.crypto.support.AbstractHash;

/**
 * @author Les Hazlewood
 * @since 1.0
 */
public abstract class HashedCredentialMatcher extends EncodingSupport implements CredentialMatcher {

    private boolean storedCredentialsHexEncoded = true; //false means base64 encoded

    /**
     * Returns <tt>true</tt> if the system's stored credential hash is Hex encoded, <tt>false</tt> if it
     * is Base64 encoded.
     *
     * <p>Default value unless overridden with the corresponding setter method is <tt>true</tt></p>
     *
     * @return <tt>true</tt> if the system's stored credential hash is Hex encoded, <tt>false</tt> if it
     *         is Base64 encoded.  Default is <tt>true</tt>
     */
    public boolean isStoredCredentialsHexEncoded() {
        return storedCredentialsHexEncoded;
    }

    /**
     * Sets the indicator if this system's stored credential hash is Hex encoded or not.
     *
     * <p>A value of <tt>true</tt> will cause this class to decode the system credential from Hex, a
     * value of <tt>false</tt> will cause this class to decode the system credential from Base64.</p>
     *
     * <p>Unless overridden via this method, the default value is <tt>true</tt>.
     *
     * @param storedCredentialsHexEncoded the indicator if this system's stored credential hash is Hex
     *                                    encoded or not ('not' automatically implying it is Base64 encoded).
     */
    public void setStoredCredentialsHexEncoded(boolean storedCredentialsHexEncoded) {
        this.storedCredentialsHexEncoded = storedCredentialsHexEncoded;
    }

    public boolean doCredentialsMatch(Object providedCredentials, Object storedCredentials) {
        Hash provided = getProvidedCredentialsHash(providedCredentials);
        Hash stored = getStoredCredentialsHash(storedCredentials);
        return stored.equals(provided);
    }

    protected abstract Hash getProvidedCredentialsHash(Object credential);

    /**
     * Returns a new, <em>uninitialized</em> instance, without its byte array set.
     *
     * @return a new, <em>uninitialized</em> instance, without its byte array set.
     */
    protected abstract AbstractHash newHashInstance();

    protected Hash getStoredCredentialsHash(Object credentials) {
        //assume stored credential is already hashed:
        AbstractHash hash = newHashInstance();

        //apply stored credentials to this Hash instance
        byte[] storedBytes = toBytes(credentials);

        if (!(credentials instanceof byte[])) {
            //method argument came in as a char[] or String, so
            //we need to do text decoding first:
            if (isStoredCredentialsHexEncoded()) {
                storedBytes = Hex.decode( storedBytes );
            } else {
                storedBytes = Base64.decodeBase64( storedBytes );
            }
        }
        hash.setBytes( storedBytes );
        return hash;
    }


}
