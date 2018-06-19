#!/usr/bin/env bash
printf 'Executing deploy script for branch %s (PR: %s)' "${TRAVIS_BRANCH}" "${TRAVIS_PULL_REQUEST}"
if [ "${TRAVIS_BRANCH}" = 'master' ] && [ "${TRAVIS_PULL_REQUEST}" == 'false' ]; then
    # Decrypt certificate
    echo 'Decrypting certificate'
    openssl aes-256-cbc -K "${encrypted_24382952c2f5_key}" -iv "${encrypted_24382952c2f5_iv}" \
    -in .travis/codesigning.asc.enc -out .travis/codesigning.asc -d
    echo 'Certificate has been successfully decrypted, importing it'
    # Import decrypted certificate
    gpg --fast-import .travis/codesigning.asc
    echo 'Certificate has been successfully imported, deploying'
    # Deploy to OSSRH repository
    mvn deploy -P sign,build-extras --settings cd/mvnsettings.xml
    echo 'Deploy successful'
else
    echo 'Deploy conditions are not met'
fi