LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := native_functions
LOCAL_SRC_FILES := native_functions.c

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE    := portmon
LOCAL_SRC_FILES := portmon.c tcp-portmon/libtcp-portmon.c

include $(BUILD_SHARED_LIBRARY)
