LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE :=open-sl
LOCAL_SRC_FILES := opensl_es_demo.c
LOCAL_LDLIBS := -lOpenSLES
include $(BUILD_SHARED_LIBRARY)

