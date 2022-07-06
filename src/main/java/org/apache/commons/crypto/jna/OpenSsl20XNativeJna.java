 /*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.crypto.jna;

import java.nio.ByteBuffer;

import org.apache.commons.crypto.Crypto;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.PointerByReference;

class OpenSsl20XNativeJna implements OpenSslInterfaceNativeJna {

    static final boolean INIT_OK;

    static final Throwable INIT_ERROR;

    static {
        boolean ok = false;
        Throwable thrown = null;
        try {
            final String libName = System.getProperty(Crypto.CONF_PREFIX + OpenSslNativeJna.class.getSimpleName(),
                    "crypto");
            OpenSslJna.debug("Native.register('%s')%n", libName);
            Native.register(libName);
            ok = true;
        } catch (final Exception | UnsatisfiedLinkError e) {
            thrown = e;
        } finally {
            INIT_OK = ok;
            INIT_ERROR = thrown;
        }
    }

    // Try to keep methods aligned across versions

    /**
     * TODO (does not appear to be used yet)
     * @return OPENSSL_VERSION_NUMBER which is a numeric release version identifier
     */
    public static native NativeLong SSLeay();

    /**
     * Retrieves version/build information about OpenSSL library.
     * This is returned by {@link OpenSslNativeJna#OpenSSLVersion(int)}
     *
     * @param type
     *            type can be SSLEAY_VERSION, SSLEAY_CFLAGS, SSLEAY_BUILT_ON...
     * @return A pointer to a constant string describing the version of the OpenSSL library or
     *         giving information about the library build.
     */
    public static native String SSLeay_version(int type);

    // TODO: NOT USED?
    /**
     * Registers the error strings for all libcrypto functions.
     */
    public static native void ERR_load_crypto_strings();

    /**
     * @return the earliest error code from the thread's error queue without modifying it.
     */
    public static native NativeLong ERR_peek_error();

    /**
     * Generates a human-readable string representing the error code e.
     *
     * @see <a href="https://www.openssl.org/docs/man1.0.2/man3/ERR_error_string.html">ERR_error_string</a>
     *
     * @param err
     *            the error code
     * @param null_
     *            buf is NULL, the error string is placed in a static buffer
     * @return the human-readable error messages.
     */
    public static native String ERR_error_string(NativeLong err, char[] null_);

    /**
     * Creates a cipher context.
     *
     * @return a pointer to a newly created EVP_CIPHER_CTX for success and NULL for failure.
     */
    public static native PointerByReference EVP_CIPHER_CTX_new();

    // TODO: NOT USED?
    /**
     * EVP_CIPHER_CTX_init() remains as an alias for EVP_CIPHER_CTX_reset
     *
     * @param p
     *            cipher context
     */
    public static native void EVP_CIPHER_CTX_init(PointerByReference p);

    /**
     * Enables or disables padding
     *
     * @param c
     *            cipher context
     * @param pad
     *            If the pad parameter is zero then no padding is performed
     * @return always returns 1
     */
    public static native int EVP_CIPHER_CTX_set_padding(PointerByReference c, int pad);

    /**
     * @return an OpenSSL AES EVP cipher instance with a 128-bit key CBC mode
     */
    public static native PointerByReference EVP_aes_128_cbc();

    /**
     * @return an OpenSSL AES EVP cipher instance with a 128-bit key CTR mode
     */
    public static native PointerByReference EVP_aes_128_ctr();

    /**
     * @return an OpenSSL AES EVP cipher instance with a 192-bit key CBC mode
     */
    public static native PointerByReference EVP_aes_192_cbc();

    /**
     * @return an OpenSSL AES EVP cipher instance with a 192-bit key CTR mode
     */
    public static native PointerByReference EVP_aes_192_ctr();

    /**
     * @return an OpenSSL AES EVP cipher instance with a 256-bit key CBC mode
     */
    public static native PointerByReference EVP_aes_256_cbc();

    /**
     * @return an OpenSSL AES EVP cipher instance with a 256-bit key CTR mode
     */
    public static native PointerByReference EVP_aes_256_ctr();

    /**
     * Init a cipher.
     *
     * @param ctx
     *            cipher context
     * @param cipher
     *            evp cipher instance
     * @param impl
     *            engine
     * @param key
     *            key
     * @param iv
     *            iv
     * @param enc
     *            1 for encryption, 0 for decryption
     * @return 1 for success and 0 for failure.
     */
    public static native int EVP_CipherInit_ex(PointerByReference ctx, PointerByReference cipher,
            PointerByReference impl, byte[] key, byte[] iv, int enc);

    /**
     * Continues a multiple-part encryption/decryption operation.
     *
     * @param ctx
     *            cipher context
     * @param bout
     *            output byte buffer
     * @param outl
     *            output length
     * @param in
     *            input byte buffer
     * @param inl
     *            input length
     * @return 1 for success and 0 for failure.
     */
    public static native int EVP_CipherUpdate(PointerByReference ctx, ByteBuffer bout, int[] outl,
            ByteBuffer in, int inl);

    /**
     * Finishes a multiple-part operation.
     *
     * @param ctx
     *            cipher context
     * @param bout
     *            output byte buffer
     * @param outl
     *            output length
     * @return 1 for success and 0 for failure.
     */
    public static native int EVP_CipherFinal_ex(PointerByReference ctx, ByteBuffer bout,
            int[] outl);

    /**
     * Clears all information from a cipher context and free up any allocated memory associate with
     * it, including ctx itself.
     *
     * @param c
     *            openssl evp cipher
     */
    public static native void EVP_CIPHER_CTX_free(PointerByReference c);

    /**
     * Clears all information from a cipher context and free up any allocated * memory associate
     * with it.
     *
     * @param c
     *            openssl evp cipher
     */
    public static native void EVP_CIPHER_CTX_cleanup(PointerByReference c);

    // Random generator
    /**
     * OpenSSL uses for random number generation
     *
     * @return pointers to the respective methods
     */
    public static native PointerByReference RAND_get_rand_method();

    /**
     * OpenSSL uses for random number generation.
     *
     * @return pointers to the respective methods
     */
    public static native PointerByReference RAND_SSLeay();

    /**
     * Generates random data
     *
     * @param buf
     *            the bytes for generated random.
     * @param num
     *            buffer length
     * @return 1 on success, 0 otherwise.
     */
    public static native int RAND_bytes(ByteBuffer buf, int num);

    // ENGINE API: https://www.openssl.org/docs/man1.0.2/man3/engine.html

    /**
     * Releases all functional references.
     *
     * @param e
     *            engine reference.
     * @return 0 on success, 1 otherwise.
     */
    public static native int ENGINE_finish(PointerByReference e);

    /**
     * Frees the structural reference
     *
     * @param e
     *            engine reference.
     * @return 0 on success, 1 otherwise.
     */
    public static native int ENGINE_free(PointerByReference e);

    /**
     * Cleanups before program exit, it will avoid memory leaks.
     *
     * @return 0 on success, 1 otherwise.
     */
    public static native int ENGINE_cleanup();

    /**
     * Obtains a functional reference from an existing structural reference.
     *
     * @param e
     *            engine reference
     * @return zero if the ENGINE was not already operational and couldn't be successfully
     *         initialized
     */
    public static native int ENGINE_init(PointerByReference e);

    /**
     * Sets the engine as the default for random number generation.
     *
     * @param e
     *            engine reference
     * @param flags
     *            ENGINE_METHOD_RAND
     * @return zero if failed.
     */
    public static native int ENGINE_set_default(PointerByReference e, int flags);

    /**
     * Gets engine by id
     *
     * @param id
     *            engine id
     * @return engine instance
     */
    public static native PointerByReference ENGINE_by_id(String id);

    // ================== instance interface methods ==================

    public boolean _INIT_OK() {
        return INIT_OK;
    }

    public Throwable _INIT_ERROR() {
        return INIT_ERROR;
    }

    public PointerByReference _ENGINE_by_id(String string) {
        return ENGINE_by_id(string);
    }

    public int _ENGINE_finish(PointerByReference rdrandEngine) {
        return ENGINE_finish(rdrandEngine);
    }

    public int _ENGINE_free(PointerByReference rdrandEngine) {
        return ENGINE_free(rdrandEngine);
    }

    public int _ENGINE_init(PointerByReference rdrandEngine) {
        return ENGINE_init(rdrandEngine);
    }

    public int _ENGINE_set_default(PointerByReference rdrandEngine, int flags) {
        return ENGINE_set_default(rdrandEngine, flags);
    }

    public String _ERR_error_string(NativeLong err, char[] buff) {
        return ERR_error_string(err, buff);
    }

    public NativeLong _ERR_peek_error() {
        return ERR_peek_error();
    }

    public PointerByReference _EVP_aes_128_cbc() {
        return EVP_aes_128_cbc();
    }

    public PointerByReference _EVP_aes_128_ctr() {
        return EVP_aes_128_ctr();
    }

    public PointerByReference _EVP_aes_192_cbc() {
        return EVP_aes_192_cbc();
    }

    public PointerByReference _EVP_aes_192_ctr() {
        return EVP_aes_192_ctr();
    }

    public PointerByReference _EVP_aes_256_cbc() {
        return EVP_aes_256_cbc();
    }

    public PointerByReference _EVP_aes_256_ctr() {
        return EVP_aes_256_ctr();
    }

    public void _EVP_CIPHER_CTX_free(PointerByReference context) {
        EVP_CIPHER_CTX_free(context);
    }

    public PointerByReference _EVP_CIPHER_CTX_new() {
        return EVP_CIPHER_CTX_new();
    }

    public int _EVP_CIPHER_CTX_set_padding(PointerByReference context, int padding) {
        return EVP_CIPHER_CTX_set_padding(context, padding);
    }

    public int _EVP_CipherFinal_ex(PointerByReference context, ByteBuffer outBuffer, int[] outlen) {
        return EVP_CipherFinal_ex(context, outBuffer, outlen);
    }

    public int _EVP_CipherInit_ex(PointerByReference context, PointerByReference algo, PointerByReference impl, byte[] encoded,
            byte[] iv, int cipherMode) {
        return EVP_CipherInit_ex(context, algo, impl, encoded, iv, cipherMode);
    }

    public int _EVP_CipherUpdate(PointerByReference context, ByteBuffer outBuffer, int[] outlen, ByteBuffer inBuffer,
            int remaining) {
        return EVP_CipherUpdate(context, outBuffer, outlen, inBuffer, remaining);
    }

    public int _RAND_bytes(ByteBuffer buf, int length) {
        return RAND_bytes(buf, length) ;
    }

    public PointerByReference _RAND_get_rand_method() {
        return RAND_get_rand_method();
    }

    public PointerByReference _RAND_SSLeay() {
        return RAND_SSLeay();
    }

    public String _OpenSSL_version(int i) {
        return SSLeay_version(i);
    }

    public void _ENGINE_load_rdrand() {
        // Not available
    }

    public int _ENGINE_cleanup() {
        return ENGINE_cleanup();
    }

    public void _EVP_CIPHER_CTX_cleanup(PointerByReference context) {
        EVP_CIPHER_CTX_cleanup(context);
    }
}