#include <jni.h>

#include "tcp-portmon/libtcp-portmon.h"

#define PEEK_BUF 50

tcp_port_monitor_collection_t *p_coll = NULL;

void
Java_org_mosspaper_objects_PortMonProvider_moninit(JNIEnv* env,
                                        jobject obj) {
    p_coll = create_tcp_port_monitor_collection();
}

jboolean
Java_org_mosspaper_objects_PortMonProvider_monadd(JNIEnv* env,
                                          jobject obj,
                                          jint startPort,
                                          jint endPort) {

    tcp_port_monitor_t *p_mon;
    tcp_port_monitor_args_t args;

    p_mon = find_tcp_port_monitor(p_coll, startPort, endPort);
    if (NULL == p_mon) {
        args.max_port_monitor_connections = 100;
        p_mon = create_tcp_port_monitor(startPort, endPort, &args);
        if (0 == insert_tcp_port_monitor_into_collection(p_coll, p_mon)) {
            return JNI_TRUE;
        }
    } 
    return JNI_FALSE;
}

jint
Java_org_mosspaper_objects_PortMonProvider_monitemlookup(JNIEnv *env,
                                                       jobject obj,
                                                       jstring jstr) {
    jint item = -1;
    const jbyte *itembuf = (*env)->GetStringUTFChars(env, jstr, NULL);

	if (strncmp(itembuf, "count", 31) == 0) {
		item = COUNT;
	} else if (strncmp(itembuf, "rip", 31) == 0) {
		item = REMOTEIP;
	} else if (strncmp(itembuf, "rhost", 31) == 0) {
		item = REMOTEHOST;
	} else if (strncmp(itembuf, "rport", 31) == 0) {
		item = REMOTEPORT;
	} else if (strncmp(itembuf, "rservice", 31) == 0) {
		item = REMOTESERVICE;
	} else if (strncmp(itembuf, "lip", 31) == 0) {
		item = LOCALIP;
	} else if (strncmp(itembuf, "lhost", 31) == 0) {
		item = LOCALHOST;
	} else if (strncmp(itembuf, "lport", 31) == 0) {
		item = LOCALPORT;
	} else if (strncmp(itembuf, "lservice", 31) == 0) {
		item = LOCALSERVICE;
	}
    (*env)->ReleaseStringUTFChars(env, jstr, itembuf);
    return item;
}

jstring
Java_org_mosspaper_objects_PortMonProvider_monpeek(JNIEnv* env,
                                           jobject obj,
                                           jint startPort,
                                           jint endPort,
                                           jint item,
                                           jint index) {

    tcp_port_monitor_t *p_mon;
    /* limited to PEEK_BUF...lame. */
    char buf[PEEK_BUF];
    memset(buf, 0, PEEK_BUF);

    p_mon = find_tcp_port_monitor(p_coll, startPort, endPort);
    if (NULL != p_mon) {
        peek_tcp_port_monitor(p_mon, item, index, buf, PEEK_BUF);
        return (*env)->NewStringUTF(env, buf);
    }
    return (*env)->NewStringUTF(env, buf);
}

void
Java_org_mosspaper_objects_PortMonProvider_monupdate(JNIEnv* env,
                                             jobject obj) {
    if (NULL != p_coll) {
        update_tcp_port_monitor_collection(p_coll);
    }
}

void
Java_org_mosspaper_objects_PortMonProvider_mondestroy(JNIEnv* env,
                                        jobject obj) {
    if (NULL != p_coll) {
        destroy_tcp_port_monitor_collection(p_coll);
    }
    p_coll = NULL;
}
