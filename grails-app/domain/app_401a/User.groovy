package app_401a
import java.time.*

class User {
    String username
    String email
    String password
    Date birthday
    //private Integer age = Period.between(birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now() ).years

    static constraints = {
        username size: 3..20, unique: true, validator: { name -> name.matches(/\w+/)
        }
        email size: 3..255, email: true, unique: true
        password size: 10..50, validator: { pwd ->
            (pwd.find(/.\p{Lu}/)                    // uppercase letter
                    && pwd.find(/\p{Ll}/)           // lowercase letter
                    && pwd.find(/\p{N}/)            // number
                    && pwd.find(/[\p{P}\p{S}]/))    // Punctuation or Symbol
            //&& pwd.length() in 10..50
        }
        birthday validator: { birthday ->           // age between 3 and 120
            Period.between(birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now() ).years in 3..120
        }
    }
}
