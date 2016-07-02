package org.approvaltests;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.approvaltests.core.ApprovalFailureReporter;
import org.approvaltests.reporters.DefaultFrontLoadedReporter;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.EnvironmentAwareReporter;
import org.approvaltests.reporters.FileLauncherReporter;
import org.approvaltests.reporters.FirstWorkingReporter;
import org.approvaltests.reporters.ImageReporter;
import org.approvaltests.reporters.MultiReporter;
import org.approvaltests.reporters.QuietReporter;
import org.approvaltests.reporters.UseReporter;
import org.packagesettings.PackageLevelSettings;
import org.packagesettings.Settings;

import com.spun.util.ClassUtils;
import com.spun.util.ObjectUtils;

public class ReporterFactory
{
  public static final String                                               FRONTLOADED_REPORTER = "FrontloadedReporter";
  private static HashMap<String, Class<? extends ApprovalFailureReporter>> reporters            = new HashMap<String, Class<? extends ApprovalFailureReporter>>();
  public static class FileTypes
  {
    public static final String  Text    = "txt";
    public static final String  Html    = "html";
    public static final String  Excel   = "csv";
    public static final String  File    = "file";
    public static final String  Image   = "png";
    private static final String Default = "default";
  }
  static
  {
    setupReporters();
  }
  public static ApprovalFailureReporter get()
  {
    ApprovalFailureReporter returned = getFromAnnotation();
    returned = tryFor(returned, reporters.get(FileTypes.Default));
    return FirstWorkingReporter.combine(getFrontLoadedReporter(), returned);
  }
  public static ApprovalFailureReporter get(String string)
  {
    ApprovalFailureReporter returned = getFromAnnotation();
    returned = tryFor(returned, reporters.get(string));
    returned = tryFor(returned, reporters.get(FileTypes.Default));
    return FirstWorkingReporter.combine(getFrontLoadedReporter(), returned);
  }
  /**
   * Loaded from PackageSettings.FrontloadedReporter
   */
  public static EnvironmentAwareReporter getFrontLoadedReporter()
  {
    Map<String, Settings> settings = PackageLevelSettings.get();
    Settings value = settings.get(FRONTLOADED_REPORTER);
    if (value != null && value.getValue() instanceof EnvironmentAwareReporter)
    {
      return (EnvironmentAwareReporter) value.getValue();
    }
    else
    {
      return DefaultFrontLoadedReporter.INSTANCE;
    }
  }
  public static ApprovalFailureReporter getFromAnnotation()
  {
    UseReporter reporter = getAnnotationFromStackTrace(UseReporter.class);
    return reporter == null ? null : getReporter(reporter);
  }
  private static ApprovalFailureReporter getReporter(UseReporter reporter)
  {
    Class<? extends ApprovalFailureReporter>[] classes = reporter.value();
    List<ApprovalFailureReporter> reporters = new ArrayList<ApprovalFailureReporter>();
    for (Class<? extends ApprovalFailureReporter> clazz : classes)
    {
      ApprovalFailureReporter instance = ClassUtils.create(clazz);
      reporters.add(instance);
    }
    return reporters.size() == 1 ? reporters.get(0) : new MultiReporter(reporters);
  }
  private static <T extends Annotation> T getAnnotationFromStackTrace(Class<T> annotationClass)
  {
    StackTraceElement[] trace = Thread.currentThread().getStackTrace();
    for (StackTraceElement stack : trace)
    {
      Method method = null;
      Class<?> clazz = null;
      try
      {
        String methodName = stack.getMethodName();
        clazz = ObjectUtils.loadClass(stack.getClassName());
        method = clazz.getMethod(methodName, (Class<?>[]) null);
      }
      catch (Throwable e)
      {
        //ignore
      }
      T annotation = null;
      if (method != null)
      {
        annotation = method.getAnnotation(annotationClass);
      }
      if (annotation != null) { return annotation; }
      if (clazz != null)
      {
        annotation = clazz.getAnnotation(annotationClass);
      }
      if (annotation != null) { return annotation; }
    }
    return null;
  }
  private static ApprovalFailureReporter tryFor(ApprovalFailureReporter returned,
      Class<? extends ApprovalFailureReporter> trying)
  {
    if (returned == null && trying != null) { return ClassUtils.create(trying); }
    return returned;
  }
  private static void setupReporters()
  {
    reporters.put(FileTypes.Text, DiffReporter.class);
    reporters.put(FileTypes.Html, DiffReporter.class);
    reporters.put(FileTypes.Excel, FileLauncherReporter.class);
    reporters.put(FileTypes.File, FileLauncherReporter.class);
    reporters.put(FileTypes.Image, ImageReporter.class);
    reporters.put(FileTypes.Default, QuietReporter.class);
  }
  public static void clearAllReportersExceptDefault()
  {
    Class all = reporters.get(FileTypes.Default);
    reporters.clear();
    reporters.put(FileTypes.Default, all);
  }
}
