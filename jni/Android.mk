LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE    := imgprocessor
LOCAL_SRC_FILES := ImgProcessor.c

include $(BUILD_SHARED_LIBRARY)