#!/usr/bin/env bash
if [ ${TRAVIS_BRANCH} = 'master' ] && [ ${TRAVIS_PULL_REQUEST} == 'false' ]; then
    # Decrypt certificate
    openssl aes-256-cbc -K ${encrypted_24382952c2f5_key} -iv ${encrypted_24382952c2f5_iv} \
    -in .travis/codesigning.asc.enc -out .travis/codesigning.asc -d
    # Import decrypted certificate
    gpg --fast-import .travis/codesigning.asc
    # Deploy to OSSRH repository
    mvn deploy -P sign,build-extras --settings cd/mvnsettings.xml
fi