/* Self generated file : do not edit */
#include <stdio.h>

@JNICHECK_INCLUDES@

static void ____jnicheck(void) {
@JNICHECK_MAIN@
}

int main(int argc, char ** argv) {
  /** This code is intended to force the compiler to resolve the function
   * ____jnicheck and all its requirements but also to avoid warnings for
   * unused variables.
   */
  void (*p)(void) = ____jnicheck;
  printf("%d %p %p", argc, argv, p);
  return 0;
}

