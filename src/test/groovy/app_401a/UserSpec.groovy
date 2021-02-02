package app_401a

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

//@SuppressWarnings(['MethodName', 'DuplicateNumberLiteral', 'DuplicateListLiteral', 'LineLength'])

class UserSpec extends Specification implements DomainUnitTest<User> {

    User validUser = new User(
            username: 'aaa',
            email: 'a@b.com',
            password: '1'*6+'ä'+'Ä'+'$'+',',
            birthday: new Date(100,1,1)
    )

    def setup() {
    }

    def cleanup() {
        if (User.count()) {
            User.findAll().each { it.delete(flush: true) }
        }
        assert User.count() == 0
    }

    void 'user with valid params should be saved'() {
        given: 'user has valid params'
        def user = validUser
        def counter = User.count()

        expect:
        with(user) {           // spock-with  !=  groovy-with, spock asserts each line, groovy doesn't
            validate()                  // 'user passes validation'
            save(flush: true)           // 'user is successfully saved'
            count() == counter + 1      // 'user is added to database'
        }
    }

    @Unroll('saving user with username: #name should have returned: #result (as boolean)')
    void 'valid username must have length in 3..20 and can only contain letters, numbers and "_"'() {
        when: 'a username is set'
        validUser.username = name

        then: 'user is either saved or not depending on the name being valid'
        (validUser.validate()
                && validUser.save()
                && User.count() == old(User.count()) + 1) == result.asBoolean()

        where:
        name                    | result
        111                     | 0         // has no letters
        'ä11'                   | 1         // valid
        'ä1'                    | 0         // too short
        'ä'+'1'*20              | 0         // too long
        'ä'+'1'*19              | 1         // valid
        'ä'+'1'*17+'Ä'+'_'      | 1         // valid
        'ä'+'1'*16+'Ä'+'_'+'$'  | 0         // contains symbol
        'ä'+'1'*16+'Ä'+'_'+','  | 0         // contains punctuation
    }

    @Unroll('saving user with password: #value should have returned: #result (as boolean)')
    void '''valid password must have length in 10..50 and must include a lower- and uppercase letter,
    a number, a symbol and the uppercase letter can not be in first place only''' () {
        when: 'a password is set'
        validUser.password = value

        then: 'user is either saved or not depending on the password being valid'
        (validUser.validate()
                && validUser.save()
                && User.count() == old(User.count()) + 1) == result.asBoolean()

        where:
        value                   | result
        'ä'+'Ä'+'$'+'1'*7       | 1         // valid
        'ä'+'Ä'+'$'+'1'*6       | 0         // too short
        'ä'+'Ä'+'$'+'1'*48      | 0         // too long
        'ä'+'Ä'+'$'+'x'*7       | 0         // no number
        'ä'+'Ä'+'1'*8           | 0         // no symbol
        'Ä'+'ä'+'$'+'x'*7       | 0         // capital letter only in first place
    }

    @Unroll('saving user with email: #address should have returned: #result (as boolean)')
    void 'valid email must have symbol "@" and a valid ending and be below 256 chars' () {
        when: 'an email is set'
        validUser.email = address

        then: 'user is either saved or not depending on the email being valid'
        (validUser.validate()
                && validUser.save()
                && User.count() == old(User.count()) + 1) == result.asBoolean()

        where:
        address                | result
        'a@b.com'              | 1         // valid
        'a$b.com'              | 0         // no "@" symbol
        'a'*70+'@b.com'        | 0         // too long
        'a'*60+'@b.com'        | 1         // valid
        'a@b.kom'              | 0         // wrong ending
    }

    @Unroll('saving user with birthday: "#date" should have returned: #result (as boolean)')
    void 'valid birthday must be a valid date and the corresponding age must be in 3..120' () {
        when: 'an birthday is set'
        validUser.birthday = date

        then: 'user is either saved or not depending on the birthday being valid'
        (validUser.validate()
                && validUser.save()
                && User.count() == old(User.count()) + 1) == result.asBoolean()

        where:
        date                        | result
        new Date()                  | 0             // too young
        new Date(-1,1,1)            | 0             // too old
        new Date(100,1,1)           | 1             // valid
    }
}
