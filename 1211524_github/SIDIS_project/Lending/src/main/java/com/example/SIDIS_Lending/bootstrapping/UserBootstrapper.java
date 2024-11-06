/*
 * Copyright (c) 2022-2024 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.example.SIDIS_Lending.bootstrapping;
import com.example.SIDIS_Lending.lendingmanagement.model.Reader;
import com.example.SIDIS_Lending.lendingmanagement.model.ReaderNumber;
import com.project.psoft.readermanagement.repositories.ReaderRepository;
import com.project.psoft.usermanagement.model.Role;
import com.project.psoft.usermanagement.model.User;
import com.project.psoft.usermanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring will load and execute all components that implement the interface
 * CommandLineRunner on startup, so we will use that as a way to bootstrap some
 * data for testing purposes.
 * <p>
 * In order to enable this bootstraping make sure you activate the spring
 * profile "bootstrap" in application.properties
 *
 * @author pgsou
 *
 */
@Component
@RequiredArgsConstructor
@Profile("bootstrap")
@Order(2)
public class UserBootstrapper implements CommandLineRunner {
    private final UserRepository userRepo;
    private final ReaderRepository readerRepo;
    private final PasswordEncoder encoder;
    private final ReaderNumber readerNumGen = new ReaderNumber();
    @Override
    @Transactional
    public void run(final String... args) throws Exception {
        //Admin
        if (userRepo.findByUsername("ranheta1@mail.com").isEmpty()) {
            final User u2 = User.newUser("ranheta1@mail.com", encoder.encode("facada"), "Ervilha", Role.ADMIN);
            userRepo.save(u2);
        }
        //Librarian
        if (userRepo.findByUsername("u1@mail.com").isEmpty()) {
            final User u1 = new User("u1@mail.com", encoder.encode("Password1"));
            u1.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(u1);
        }
        if (userRepo.findByUsername("mary@mail.com").isEmpty()) {
            final User u3 = new User("mary@mail.com", encoder.encode("Password1"));
            u3.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(u3);
        }

        //Reader
        if (readerRepo.findByUsername("mary1@mail.com").isEmpty()) {
            final User us1 = new User("mary1@mail.com", encoder.encode("myMy123!"));
            final Reader r1 = Reader.newReader("mary1@mail.com", "Mary 1","02/12/2002","123456789","yes",us1);
            r1.setReaderNumber(readerNumGen.generate(readerNumGen.getNextReaderNumber(readerRepo)));
            userRepo.save(us1);
            readerRepo.save(r1);
        }
        if (readerRepo.findByUsername("mary2@mail.com").isEmpty()) {
            final User us2 = new User("mary2@mail.com", encoder.encode("myMy123!"));
            final Reader r2 = Reader.newReader("mary2@mail.com","Mary 2","02/12/2002","123456789","yes",us2);
            r2.setReaderNumber(readerNumGen.generate(readerNumGen.getNextReaderNumber(readerRepo)));
            userRepo.save(us2);
            readerRepo.save(r2);
        }
        if (readerRepo.findByUsername("mary3@mail.com").isEmpty()) {
            final User us3 = new User("mary3@mail.com", encoder.encode("myMy123!"));
            final Reader r3 = Reader.newReader("mary3@mail.com", "Mary 3","02/12/2002","123456789","yes", us3);
            r3.setReaderNumber(readerNumGen.generate(readerNumGen.getNextReaderNumber(readerRepo)));
            userRepo.save(us3);
            readerRepo.save(r3);
        }
        if (readerRepo.findByUsername("mary4@mail.com").isEmpty()) {
            final User us4 = new User("mary4@mail.com", encoder.encode("myMy123!"));
            final Reader r4 = Reader.newReader("mary4@mail.com", "Mary 4","02/12/2002","123456789","yes",us4);
            r4.setReaderNumber(readerNumGen.generate(readerNumGen.getNextReaderNumber(readerRepo)));
            userRepo.save(us4);
            readerRepo.save(r4);
        }
        if (readerRepo.findByUsername("mary5@mail.com").isEmpty()) {
            final User us5 = new User("mary5@mail.com", encoder.encode("myMy123!"));
            final Reader r5 = Reader.newReader("mary5@mail.com", "Mary 5","02/12/2002","123456789","yes",us5);
            r5.setReaderNumber(readerNumGen.generate(readerNumGen.getNextReaderNumber(readerRepo)));
            userRepo.save(us5);
            readerRepo.save(r5);
        }
        if (readerRepo.findByUsername("mary6@mail.com").isEmpty()) {
            final User us6 = new User("mary6@mail.com", encoder.encode("myMy123!"));
            final Reader r6 = Reader.newReader("mary6@mail.com", "Mary 6","02/12/2002","123456789","yes",us6);
            r6.setReaderNumber(readerNumGen.generate(readerNumGen.getNextReaderNumber(readerRepo)));
            userRepo.save(us6);
            readerRepo.save(r6);
        }

    }
}
