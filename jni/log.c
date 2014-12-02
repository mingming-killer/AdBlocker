/**
 * The log utils.
 * @author humm <humm@dw.gdbbk.com>
 */

#include <stdio.h>
#include <string.h>

#include "log.h"

#if (DEBUG)
	// TODO: adjust to match debug string length.
	#define DEBUG_STRING_BUFFER (1024 * 8)
#else
	#define DEBUG_STRING_BUFFER (1)
#endif

static char g_str_debug_buff[DEBUG_STRING_BUFFER] = {0};
static char g_str_debug_private_buff[DEBUG_STRING_BUFFER] = {0};

char* debug_string(void) {
	return g_str_debug_buff;
}

char* reset_debug_string(void) {
	memset(g_str_debug_buff, 0x00, strlen(g_str_debug_buff));
	return g_str_debug_buff;
}

char* debug_string_private(void) {
	return g_str_debug_private_buff;
}

void debug_show_bytes(unsigned char* pbytes, int nlen) {
	int i = 0;
	unsigned char* p = pbytes;

	if (NULL == p) {
		return;
	}

	//LOG_D_V("debugShow 1");
	//while (null != p && 0 != *p) {
	//	sprintf(g_strDebugPrivateBuff, "%02x, ", *p);
	//	strcat(debugString(), g_strDebugPrivateBuff);
	//	p += 1;
	//}

	sprintf(debug_string(), "bytes address: 0x%8x, len: %d", p, nlen);
	LOG_D_V(debug_string());

	memset(g_str_debug_buff, 0x00, strlen(g_str_debug_buff));
	memset(g_str_debug_private_buff, 0x00, strlen(g_str_debug_private_buff));

	for (i = 0; i < nlen; i++) {
		if (NULL == p) {
			break;
		}

		sprintf(g_str_debug_private_buff, "%02x, ", *p);
		strcat(debug_string(), g_str_debug_private_buff);
		p += 1;
	}

	//LOG_D_V("debugShow 3");
	LOG_D_V(debug_string());
}
