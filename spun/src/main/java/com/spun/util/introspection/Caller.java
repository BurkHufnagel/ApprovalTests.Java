package com.spun.util.introspection;

import com.spun.util.ThreadUtils;

public class Caller
{
  public static StackTraceElement get(int levelsAboveMe)
  {
    return ThreadUtils.getStackTrace()[3 + levelsAboveMe];
  }
}
