#!/usr/bin/env bash
if [ ${TRAVIS_BRANCH} = 'master' ] && [ ${TRAVIS_PULL_REQUEST} == 'false' ]; then
    # Decrypt certificate
    openssl aes-256-cbc -K ${encrypted_8a1cf5e3e25e_key} -iv ${encrypted_8a1cf5e3e25e_iv} \
    -in .travis/codesigning.asc.enc -out .travis/codesigning.asc -d
    # Import decrypted certificate
    gpg --fast-import .travis/codesigning.asc
    # Deploy to OSSRH repository
    mvn deploy -P sign,build-extras --settings cd/mvnsettings.xml
fi