/*
	Message Digest (Hash function)	
*/
#ifndef _JLG_MD_H_
#define _JLG_MD_H_
#include "jlg.h"

int jlg_md_str(const char *src, char *dst, int md_length, const char *algo_name);

#endif // _JLG_MD_H_