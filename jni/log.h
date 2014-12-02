/**
 * The log utils.
 * @author humm <humm@dw.gdbbk.com>
 */

#ifndef LOG_H
#define LOG_H

#include <stdio.h>
#include <android/log.h>

#ifdef  __cplusplus
extern  "C"
{
#endif

/** TODO: for release version set it to 0. */
#define DEBUG 1

/** TODO: set to your application tag. */
#define LOG_TAG 	"RootKit"    /** log tag. */
#undef  LOG                      /** cancel default log tag. */

char* reset_debug_string(void);
char* debug_string(void);

void debug_show_bytes(unsigned char* pbytes, int nlen);
char* debug_string_private(void);


#if (DEBUG)
	#define LOG_D(...) 	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)  /** Android Log.d */
	#define LOG_I(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)   /** Android Log.i */
	#define LOG_W(...)  __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)   /** Android Log.w */
	#define LOG_E(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)  /** Android Log.e */
	#define LOG_F(...)  __android_log_print(ANDROID_LOG_FATAL, LOG_TAG, __VA_ARGS__)  /** Android Log.f */

	/** for trace log in file line. */
	#define LOG_D_V(string) {                                                   \
		if (NULL != string) {                                                    \
			memset(debug_string_private(), 0, strlen(debug_string_private()));       \
			sprintf(debug_string_private(), "%s: line: %d: ", __FILE__, __LINE__); \
			strcat(debug_string_private(), (string));                              \
			LOG_D(debug_string_private());                                         \
		}                                                                        \
	}

#else
	#define LOG_D(...) ;
	#define LOG_I(...) ;
	#define LOG_W(...) ;
	#define LOG_E(...) ;
	#define LOG_F(...) ;

	#define LOG_D_V(string) ;

#endif


#ifdef  __cplusplus
extern  "C"
}
#endif

#endif
