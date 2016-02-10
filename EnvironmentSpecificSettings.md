#Make variables specific to different environments.
# Introduction #

Being able to specify the values of variables based on the environment being used is useful for cases such as the thresholds used in finding similar interests - if we can keep the values constant for the test environment, we can try different values for the actual database without breaking the tests.


# Details #

## Config ##
In the Config file (found in the grails-app/conf folder), you'll find a section at the very bottom that is preceded by a comment saying "// environment specific settings." To set default values for the variables (ones that will be used if not environment-specific values are provided) define the variables above said comment. Then, for each environment for which you want a different value, redefine the variable inside the relevant section after the "environments {" heading.

**As an example, look at this section of code** (NOTE: values aren't meaningful and spacing is strange)**:**

absoulteThreshold = 0.10

refinedThreshold = 0.06

roughThreshold = 0.10

//environment specific settings

environments {
> development {
> > // Uncomment to rebuild db


> //prepDirectories("dev")
> }
> test {
> > prepDirectories("test")


> absoluteThreshold = 0.15

> refinedThreshold = 0.08

> roughThreshold = 0.15
> }
> populateTest{

> absoluteThreshold = 0.15

> refinedThreshold = 0.08

> roughThreshold = 0.15
> }
> populate {

> }

> production {

> }
}


If the environment that you want to define variables for isn't shown in this section, add it (as I did with populateTest above).

## Class where variable is used ##
In the class where you use the variable, you need to define it with a pointer to the Config file. You do this by importing "org.codehaus.groovy.grails.commons.ConfigurationHolder" and then defining your variable as: "ConfigurationHolder.config.variableName".

**For example:**

double roughThreshold = ConfigurationHolder.config.roughThreshold