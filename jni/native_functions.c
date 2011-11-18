#include <jni.h>
#include <locale.h>
#include <string.h>
#include <sys/vfs.h>
#include <sys/utsname.h>
#include <time.h>

void
Java_org_mosspaper_objects_FSJni_getFsInfo(JNIEnv* env,
                                           jobject obj,
                                           jstring jpath,
                                           jobject fs_obj)
{
    jfieldID type_fid, bsize_fid, blocks_fid, bfree_fid, bavail_fid;

    const jbyte *path = (*env)->GetStringUTFChars(env, jpath, NULL);
    if (NULL == path) {
        return;
    }
    jclass fs_cls = (*env)->GetObjectClass(env, fs_obj);

    type_fid = (*env)->GetFieldID(env, fs_cls, "f_type", "J");
    bsize_fid = (*env)->GetFieldID(env, fs_cls, "f_bsize", "J");
    blocks_fid = (*env)->GetFieldID(env, fs_cls, "f_blocks", "J");
    bfree_fid = (*env)->GetFieldID(env, fs_cls, "f_bfree", "J");
    bavail_fid = (*env)->GetFieldID(env, fs_cls, "f_bavail", "J");

    struct statfs f;
    if (statfs(path, &f) == 0) {
        if (NULL != type_fid) {
            (*env)->SetLongField(env, fs_obj, type_fid, f.f_type);
        }
        if (NULL != bsize_fid) {
            (*env)->SetLongField(env, fs_obj, bsize_fid, f.f_bsize);
        }
        if (NULL != blocks_fid) {
            (*env)->SetLongField(env, fs_obj, blocks_fid, f.f_blocks);
        }
        if (NULL != bfree_fid) {
            (*env)->SetLongField(env, fs_obj, bfree_fid, f.f_bfree);
        }
        if (NULL != bavail_fid) {
            (*env)->SetLongField(env, fs_obj, bavail_fid, f.f_bavail);
        }
    }
    (*env)->ReleaseStringUTFChars(env, jpath, path);
}

void
Java_org_mosspaper_objects_UnameProvider_setUname(JNIEnv* env,
                                                  jobject obj)
{
    jstring jstr;
    jfieldID sysname, nodename, release, version, machine;

    jclass cls = (*env)->GetObjectClass(env, obj);
    sysname = (*env)->GetFieldID(env, cls, "sysname", "Ljava/lang/String;");
    nodename = (*env)->GetFieldID(env, cls, "nodename", "Ljava/lang/String;");
    release = (*env)->GetFieldID(env, cls, "release", "Ljava/lang/String;");
    version = (*env)->GetFieldID(env, cls, "version", "Ljava/lang/String;");
    machine = (*env)->GetFieldID(env, cls, "machine", "Ljava/lang/String;");

    struct utsname u;
    if (uname(&u) == 0) {
        jstr = (*env)->NewStringUTF(env, u.sysname);
        if (NULL != sysname && NULL != jstr) {
            (*env)->SetObjectField(env, obj, sysname, jstr);
        }
        jstr = (*env)->NewStringUTF(env, u.nodename);
        if (NULL != nodename && NULL != jstr) {
            (*env)->SetObjectField(env, obj, nodename, jstr);
        }
        jstr = (*env)->NewStringUTF(env, u.release);
        if (NULL != release && NULL != jstr) {
            (*env)->SetObjectField(env, obj, release, jstr);
        }
        jstr = (*env)->NewStringUTF(env, u.version);
        if (NULL != version && NULL != jstr) {
            (*env)->SetObjectField(env, obj, version, jstr);
        }
        jstr = (*env)->NewStringUTF(env, u.machine);
        if (NULL != machine && NULL != jstr) {
            (*env)->SetObjectField(env, obj, machine, jstr);
        }
    }
}

jstring
Java_org_mosspaper_objects_Time_strftime(JNIEnv* env,
                                         jobject obj,
                                         jstring jformat)
{
    char buf[128];
    const jbyte *format = (*env)->GetStringUTFChars(env, jformat, NULL);
    if (NULL == format) {
        return;
    }
    time_t t = time(NULL);
    struct tm *tm = localtime(&t);
    setlocale(LC_TIME, "");
    strftime(buf, 128, format, tm);
    
    (*env)->ReleaseStringUTFChars(env, jformat, format);
    return (*env)->NewStringUTF(env, buf);
}

