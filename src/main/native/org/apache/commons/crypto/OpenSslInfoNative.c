/**
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

// identify caller to the include file (to avoid unnecessary define of _GNU_SOURCE)
#define ORG_APACHE_COMMONS_OPENSSLINFONATIVE_C
#include "org_apache_commons_crypto.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef UNIX
#include <unistd.h>
#include <sys/types.h>
#endif

#ifdef WINDOWS
#include <windows.h>
#endif

// export the native interfaces
#ifdef JNIEXPORT
#undef JNIEXPORT
#endif
#define JNIEXPORT __attribute__((__visibility__("default")))
#include "org_apache_commons_crypto_OpenSslInfoNative.h"

#ifdef UNIX
static unsigned long (*dlsym_OpenSSL_version_num) (void);
static const char * (*dlsym_OpenSSL_version) (int);
#endif

#ifdef WINDOWS
typedef unsigned long (__cdecl *__dlsym_OpenSSL_version_num) (void);
typedef char * (__cdecl *__dlsym_OpenSSL_version) (int);
static __dlsym_OpenSSL_version_num dlsym_OpenSSL_version_num;
static __dlsym_OpenSSL_version dlsym_OpenSSL_version;
#endif

static char dynamicLibraryPath[80];  // where was the crypto library found?

static void get_methods(JNIEnv *env, HMODULE openssl)
{
  LOAD_DYNAMIC_SYMBOL_FALLBACK(__dlsym_OpenSSL_version_num, dlsym_OpenSSL_version_num, env, openssl, "OpenSSL_version_num", "SSLeay"); // SSLeay fallback needed by LibreSSL 2.x
  LOAD_DYNAMIC_SYMBOL_FALLBACK(__dlsym_OpenSSL_version, dlsym_OpenSSL_version, env, openssl, "OpenSSL_version", "SSLeay_version"); // SSLeay fallback needed by LibreSSL 2.x
#ifdef UNIX
  Dl_info info;
  (void) dladdr(dlsym_OpenSSL_version_num, &info); // ignore the return code
  strncpy(dynamicLibraryPath, info.dli_fname, sizeof(dynamicLibraryPath) - 1); // allow for null
#endif
#ifdef WINDOWS
  LPWSTR lpFilename;
  WCHAR buffer[80];
  lpFilename = buffer;
  (void) GetModuleFileName(openssl, lpFilename, sizeof(buffer)); // ignore return code
  wcstombs(dynamicLibraryPath, buffer, sizeof(dynamicLibraryPath));
  #endif
}

static int loaded = 0; // have we successfully loaded the library and the methods?

static int load_library(JNIEnv *env)
{
  HMODULE openssl = open_library(env); // calls THROW and returns 0 on error

  if (!openssl) {
    return 0;
  }
  if (!loaded) {
    get_methods(env, openssl);
    loaded = 1;
  }
  return 1;
}

JNIEXPORT jstring JNICALL Java_org_apache_commons_crypto_OpenSslInfoNative_NativeVersion
  (JNIEnv *env, jobject object)
{
  return (*env)->NewStringUTF(env, VERSION);
}

JNIEXPORT jstring JNICALL Java_org_apache_commons_crypto_OpenSslInfoNative_NativeTimeStamp
  (JNIEnv *env, jobject object)
{
  return (*env)->NewStringUTF(env, __DATE__);
}

JNIEXPORT jstring JNICALL Java_org_apache_commons_crypto_OpenSslInfoNative_NativeName
  (JNIEnv *env, jobject object)
{
  return (*env)->NewStringUTF(env, PROJECT_NAME);
}

JNIEXPORT jstring JNICALL Java_org_apache_commons_crypto_OpenSslInfoNative_HeaderVersionText
  (JNIEnv *env, jobject object)
{
  return (*env)->NewStringUTF(env, OPENSSL_VERSION_TEXT); // from the OpenSSL header files
}

JNIEXPORT jlong JNICALL Java_org_apache_commons_crypto_OpenSslInfoNative_HeaderVersionNumber
  (JNIEnv *env, jclass clazz)
{
  return OPENSSL_VERSION_NUMBER; // from the OpenSSL header files
}

JNIEXPORT jlong JNICALL Java_org_apache_commons_crypto_OpenSslInfoNative_OpenSSL
  (JNIEnv *env, jclass clazz)
{
  if (!load_library(env)) {
    return 0;
  }
  jlong version_num = (jlong)dlsym_OpenSSL_version_num();
  return version_num;
}

JNIEXPORT jstring JNICALL Java_org_apache_commons_crypto_OpenSslInfoNative_OpenSSLVersion
  (JNIEnv *env, jclass clazz, jint type)
{
  if (!load_library(env)) {
    return NULL;
  }
  jstring answer = (*env)->NewStringUTF(env,dlsym_OpenSSL_version(type));
  return answer;
}

JNIEXPORT jstring JNICALL Java_org_apache_commons_crypto_OpenSslInfoNative_DLLName
  (JNIEnv *env, jclass clazz)
{
  jstring answer = (*env)->NewStringUTF(env, COMMONS_CRYPTO_OPENSSL_LIBRARY);
  return answer;
}

JNIEXPORT jstring JNICALL Java_org_apache_commons_crypto_OpenSslInfoNative_DLLPath
  (JNIEnv *env, jclass clazz)
{
  if (!load_library(env)) {
    return NULL;
  }
  jstring answer = (*env)->NewStringUTF(env, dynamicLibraryPath);
  return answer;
}
