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
package com.example.SIDIS_Reader.bootstrapping;
import com.example.SIDIS_Reader.readermanagement.model.Reader;
import com.example.SIDIS_Reader.readermanagement.model.ReaderNumber;
import com.example.SIDIS_Reader.readermanagement.repositories.ReaderRepository;
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
public class ReaderBootstrapper implements CommandLineRunner {

    private final ReaderRepository readerRepo;
    private final PasswordEncoder encoder;
    private final ReaderNumber readerNumGen = new ReaderNumber();
    @Override
    @Transactional
    public void run(final String... args) throws Exception {

        //Reader
        if (readerRepo.findByUsername("mary1@mail.com").isEmpty()) {
            final Reader r1 = Reader.newReader("mary1@mail.com", "Mary","02/12/2002","123456789","yes");
            r1.setReaderNumber(readerNumGen.generate(readerNumGen.getNextReaderNumber(readerRepo)));
            readerRepo.save(r1);
        }
        if (readerRepo.findByUsername("mary2@mail.com").isEmpty()) {
            final Reader r2 = Reader.newReader("mary2@mail.com","Mary 2","02/12/2002","123456789","yes");
            r2.setReaderNumber(readerNumGen.generate(readerNumGen.getNextReaderNumber(readerRepo)));
            readerRepo.save(r2);
        }
        if (readerRepo.findByUsername("mary3@mail.com").isEmpty()) {
            final Reader r3 = Reader.newReader("mary3@mail.com", "Mary 3","02/12/2002","123456789","yes");
            r3.setReaderNumber(readerNumGen.generate(readerNumGen.getNextReaderNumber(readerRepo)));
            readerRepo.save(r3);
        }
    }
}
