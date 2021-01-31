package app_401a
import java.time.*

class User {
    String username
    String email
    String password
    Date birthday
    private Integer age = Period.between(birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now() ).years

    static constraints = {
        username size: 3..20, unique: true, validator: { name -> name.matches(/\w+/)
        }
        email size: 3..255, email: true, unique: true
        password size: 10..50, validator: { pwd ->
            (pwd.find(/.\p{Lu}/)
                    && pwd.find(/\p{Ll}/)
                    && pwd.find(/\p{N}/)
                    && pwd.find(/[\p{P}\p{S}]/))
            //&& pwd.length() in 10..50
        }
        age range: 3..120
    }
}
