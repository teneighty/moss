LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := native_functions
LOCAL_SRC_FILES := native_functions.c

include $(BUILD_SHARED_LIBRARY)

