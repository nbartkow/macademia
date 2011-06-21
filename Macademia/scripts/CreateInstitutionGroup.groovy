import org.macademia.*

InstitutionGroup.withTransaction {
    println('')
    while ( true ) {
        InstitutionGroup ig1 = checkSelection( readSelectionCode() )
        String inputs = new String()
        if( !ig1 ) {
            println('Run Complete\n')
            break // Exit Main loop
        }
        while( true ) {
            inputs = readText( "Enter name/domain of institution to add or 'end' " )
            if( inputs == 'end') {
                break
            }
            Institution school = Institution.findByEmailDomain( inputs )
            if ( !school ) {
                school = Institution.findByName( inputs )
            }
            if( !school ) {
                println("Error: No such institution found in database!")
            } else if(!ig1.institutions || !ig1?.institutions?.contains(school)){
                ig1.addToInstitutions( school )
                school.addToInstitutionGroups( ig1 )
                Utils.safeSave( school )
            } else {
                println("Institution '${school.name}' is already a member of group '${ig1.name}'!")
            }
        }
        Utils.safeSave( ig1 )
        System.console().flush()
    }
}

int readSelectionCode() {
    int responseCode = stringToInt( readText(
        """
            Institution Group Management\n
            Choose an option from the menu:\n
            \t1) Create a new InstitutionGroup\n
            \t2) Add an Institution to an existing InstitutionGroup\n
            \t3) End
            Enter number of selection:
        """) )
    return responseCode
}

InstitutionGroup checkSelection( int selectionCode ) {
    InstitutionGroup ig1 = null
    while( true ) {
        def igText = []

        if( selectionCode == 1){
            // Handles case of creating a new IG. Checks that IG does not already exist
            String igName, igAbbrev

            while( (igText as List<String>).size() != 2 ) {
                if( !(igText as List<String>).isEmpty() ) {
                    println("Error: Response must in the form 'Group, abbreviation' ")
                }
                igText = readText( "Enter 'Name' of new InstitutionGroup and group 'Abbreviation', separated by a comma: " ).split(',')
            }
            igName = igText[0]?.trim()
            igAbbrev = igText[1].trim().toLowerCase()

            if( !InstitutionGroup.findByAbbrev( igAbbrev ) ) {
                ig1 = new InstitutionGroup(name : igName, abbrev : igAbbrev )
                break
            } else {
                selectionCode = stringToInt( readText("\nInstitutionGroup with Abbrev $igAbbrev already exists. Choose another menu option: ") )
                igText=[]
            }
        } else if ( selectionCode == 2 ) {
            // Handles case of adding an Institution to an existing IG. Checks if IG exists
            String igAbbrev = readText( "Enter group 'Abbreviation': " )
            ig1 = InstitutionGroup.findByAbbrev( igAbbrev )

            if(!ig1) {
                selectionCode = stringToInt( readText("\nInstitutionGroup with Abbrev $igAbbrev does not exist. Choose another option: ") )
            } else {
                break
            }
        } else if ( selectionCode == 3 ) {
            // User selects 'End'. Break out of while loop and return 'escape = true' to main method
            escape = true
            break
        } else {
            // Handle typos/invalid entries
            selectionCode = stringToInt( readText('Invalid option. Enter number of selection: ') )
        }
    }
    return ig1
}

String readText(String prompt) {
    println( prompt );
    return System.console().readLine()
}

int stringToInt(String string) {
    try {
        int num = string.toInteger()
        return num
    }
    catch(NumberFormatException) {
        return 0
    }
}
