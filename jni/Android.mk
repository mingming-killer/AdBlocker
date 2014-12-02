LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := rootkit
LOCAL_SRC_FILES := \
	log.c \
	rootkit.c
	
LOCAL_LDLIBS    := -llog 

# the ndk r9 default open the format string check, we show disable it, 
# otherwise can't pass the compile.
#LOCAL_DISABLE_FORMAT_STRING_CHECKS=true

include $(BUILD_SHARED_LIBRARY)
