package org.sonatype.sisu.goodies.common;

/**
 * A simple {@link String#format(String, Object...)} based template that defers "calculation" until needed. Usable when
 * you need same template multiple times, for example to log a message and then throw an exception with same message,
 * but is not restricted to these cases.
 * <p>
 * With Guava:
 * 
 * <pre>
 * Preconditions.checkArgument( i &gt; 0, Template.of( &quot;The parameter i=%s is not positive!&quot;, i ) );
 * </pre>
 * 
 * The evaluation of Template will happen only when needed, thus, avoiding potentially unneeded
 * {@link String#format(String, Object...)} call. Next, a hokey-ish but workable solution with SLF4J to buy out Varargs:
 * 
 * <pre>
 * log.info("{}", Template.of("My %s", "format"))}
 * </pre>
 * 
 * Here also, you gain the fact that SLF4J with evaluate the template only if call is about to result in actual log (ie.
 * is "above" current logging level). Or something like:
 * 
 * <pre>
 * final Template t = Template.of( &quot;My %s message&quot;, &quot;customized&quot; );
 * log.debug( &quot;{}&quot;, t );
 * throw new Exception( t.toString() );
 * </pre>
 * 
 * Where you can reuse same message.
 * 
 * @author cstamas
 * @since 1.3
 */
public class Template
{
    private final String format;

    private final Object[] args;

    /**
     * Creates a new instance of Template.
     * 
     * @param format The format to be used, {@code null} not allowed.
     * @param args Zero or more arguments for the template.
     */
    public Template( final String format, final Object... args )
    {
        if ( null == format )
        {
            throw new NullPointerException( "Template's format cannot be null!" );
        }
        this.format = format;
        this.args = args;
    }

    /**
     * Performs template evaluation, is "heavy" operation and should be deferred as long possible.
     * 
     * @return The message after applied {@link String#format(String, Object...)} using current format and arguments.
     */
    public String evaluate()
    {
        return String.format( format, args );
    }

    public String getFormat()
    {
        return format;
    }

    public Object[] getArgs()
    {
        return args;
    }

    public String toString()
    {
        return evaluate();
    }

    // --

    /**
     * A static helper method to create templates fluently.
     * 
     * @param format The template format.
     * @param args The vararg for arguments.
     * @return A constructed Template instance.
     */
    public static Template of( final String format, final Object... args )
    {
        return new Template( format, args );
    }
}
