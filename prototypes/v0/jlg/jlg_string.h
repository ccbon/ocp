/* purpose of properties is to load easily a properties file that configure an application */
#ifndef _JLG_STRING_H_
#define _JLG_STRING_H_

// like chomp in perl.
void chomp(char *string);

// substring
// buffer: destination where the substring is stored.
// src : the original string where to extract the substring
// start : the start point.
// the length of the string.
void strsub(char *buffer, const char *src, int start, int length);

void strsplit(char ***arrayp,	int *length, const char *line, const char *sep);
	
void strsplit_free(char ***arrayp);

#endif // _JLG_STRING_H_