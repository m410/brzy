package org.brzy.fab.task;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *  Annotates an individual task in a phase or plugin.  Tasks are an single unit of work.
 *
 * A task generally takes no parameters.  When a task is a member of a phase it cannot take
 * arguments.  When a task is dependent on other tasks it cannot have any arguments
 * either.  Tasks that are members of plugins that are called directly by the command line
 * interface can take any number of arguments.
 *
 * @author Michael Fortin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Task {

    /**
     * the unique name of the task.  The name must be unigue to other tasks, phases and
     * plugin names.
     *
     * @return name
     */
    String name();

    /**
     * description used in the calling fab -tasks
     * @return description
     */
    String desc() default "";

    /**
     * The names of the other tasks that must run before this task can run.
     * @return array of task names
     */
	String[] dependsOn() default "";

    /**
     * The names of the task that must run after this task runs.  This is generally only used
     * when injecting tasks into phases when the task needs to run in after a task but before
     * others.
     *
     * @return task name
     */
    String belongsTo() default "";
}