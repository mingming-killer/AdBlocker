/**
 * @file 	BaseDataType.h
 * @brief	基本数据定义。
 * @version H600S
 * @author 	humingming <humingming@oaserver.dw.gdbbk.com>
 * @date	2012/12/26
 */

#ifndef BASE_DATA_TYPE_H
#define BASE_DATA_TYPE_H


#ifdef  __cplusplus
extern  "C"
{
#endif


#ifndef null
	#define null NULL
#endif

#ifndef NULL
	#define NULL ((void*)0)
#endif

#ifndef bool
	#define bool  int
	#define true  1
	#define false 0
#endif

#ifndef BOOL
	#define BOOL int
	#define TRUE 1
	#define FALSE 0
#endif


typedef unsigned char     U8;
typedef unsigned short    U16;
typedef unsigned int 	  U32;

typedef signed char       S8;
typedef signed short      S16;
typedef U32               RGB;
typedef void*             PVOID;

typedef signed char       T_BYTE;     // *8-bit signed integer
typedef unsigned char     T_UBYTE;    // *8-bit unsigned integer
typedef signed short      T_HWORD;    // *16-bit signed integer
typedef unsigned short    T_UHWORD;   // *16-bit unsigned integer
typedef signed int        T_WORD;     // *32-bit signed integer
typedef unsigned int      T_UWORD;    // *32-bit unsigned integer


#ifndef UINT_SIZE
	#define UINT_SIZE sizeof(unsigned int)
#endif

#ifndef UCAHR_SIZE
	#define UCHAR_SIZE sizeof(unsigned char)
#endif

#ifndef ULONG_SIZE
	#define ULONG_SIZE sizeof(unsigned long)
#endif

#ifndef TABLESIZE
	#define TABLESIZE(table) (sizeof(table) / sizeof(table[0]))
#endif


#ifdef  __cplusplus
extern  "C"
}
#endif

#endif
