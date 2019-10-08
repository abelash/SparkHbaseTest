package org.abelashapps.spark.example

trait ResourceAware {

  /**
   * Get own resource by file name. Resource should be in scr/test/scala/resources/{test_suite_class_name}
   */
  def resource(resourceName: String): String = {
    getClass.getClassLoader.getResource(s"$getClass.getSimpleName/$resourceName").getFile
  }

  /**
   * Get resource from anywhere in classpath
   */
  def resourceFromClasspath(resourceName: String): String = {
    getClass.getClassLoader.getResource(resourceName).getFile
  }
}
