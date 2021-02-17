import os
import re
import sys

GRADLE_FILE = 'build.gradle'
VERSION_NAME_PATTERN = 'def *appVersionName *=  *\"(.*)\"'
VERSION_CODE_PATTERN = 'def *appVersionCode *= *(28(\d\d)(\d\d)(\d\d)(\d))'

VERSION_EXAMPLE_PATTERN = '\/\/ *eg. *\((\d*)\) *-> *MAJOR *APP *VER: *(\d\d) *\| *BETA APP VERSION: *(\d\d) *\| *MINOR APP VERSION: *(\d\d) *\| *PATCH *VERSION: *(\d)'

VERSION_NAME_REPLACEMENT = 'def appVersionName = "{0}"'
VERSION_CODE_REPLACEMENT = 'def appVersionCode = {0}'
VERSION_EXAMPLE_REPLACEMENT = '// eg. ({0}) -> MAJOR APP VER: {1} | BETA APP VERSION: {2} | MINOR VERSION: {3} | PATCH VERSION: {4}'

VERSION_CODE_PREFIX = '28'

UPDATE_TYPE_MINOR = 'minor'
UPDATE_TYPE_PATCH = 'patch'
UPDATE_TYPE_BETA = 'beta'

UPDATE_TYPE = os.environ.get('UPDATE_TYPE','')
BRANCH = os.environ.get('BRANCH')

def error(message='Something Went Wrong!'):
    print(message)
    sys.exit(1)

def get_new_version_code(file_data):
    code_matches = (re.findall(VERSION_CODE_PATTERN, file_data)[0])
    print("Old Version Code: {0}".format( code_matches[0]))

    major = int(code_matches[1])
    beta = int(code_matches[2])
    minor = int(code_matches[3])
    patch = int(code_matches[4])

    print("major,beta,minor,patch: {0} {1} {2} {3}".format(major,beta,minor,patch))

    if UPDATE_TYPE == UPDATE_TYPE_MINOR:
        minor+=1
        patch=0
    elif UPDATE_TYPE == UPDATE_TYPE_BETA:
        beta+=1
        minor=0
        patch=0
    elif UPDATE_TYPE == UPDATE_TYPE_PATCH:
        patch+=1
    else:
        sys.exit(1)
    new_version_code = '%s%02d%02d%02d%01d' % (VERSION_CODE_PREFIX, major, beta, minor, patch)
    print("New Version Code: {0}".format(new_version_code))
    return new_version_code

def get_new_version_name(file_data):
    version_name = re.findall(VERSION_NAME_PATTERN, file_data)[0]
    print("Old Version Name: {0}".format(version_name))
    version_tokens = version_name.split('.')
    majorName = version_tokens[0]
    betaName  = version_tokens[1]

    code_matches = (re.findall(VERSION_CODE_PATTERN, file_data)[0])
    beta = int(code_matches[2])
    minor = int(code_matches[3])
    patch = int(code_matches[4])

    new_version_tokens = []

    if UPDATE_TYPE == UPDATE_TYPE_MINOR:
        new_minor = minor + 1
        if new_minor > 99:
            error('Minor can\'t be more than 99')
        new_version_tokens.append(majorName)
        new_version_tokens.append(betaName)
        new_version_tokens.append(str(new_minor))
    elif UPDATE_TYPE == UPDATE_TYPE_BETA:
        newBeta = beta + 1
        if newBeta > 99:
            error('Normal Release can\'t be more than 99')
        new_version_tokens.append(majorName)
        new_version_tokens.append(str(newBeta)+'-beta')
        new_version_tokens.append(str(minor))
    elif UPDATE_TYPE == UPDATE_TYPE_PATCH:
        newPatch = patch+1
        if newPatch > 9:
            error('Patch can\'t be more than 9')
        new_version_tokens.append(majorName)
        new_version_tokens.append(betaName)
        new_version_tokens.append(str(minor)+'-patch-'+str(newPatch))
    else:
        error("Invalid Update Type")

    new_version_name = '.'.join(new_version_tokens)
    print("New Version Name: {0}".format(new_version_name))
    return new_version_name

def cli():
    os.system('git checkout {0}'.format(BRANCH))
    os.system('git pull origin {0}'.format(BRANCH))

    with open(GRADLE_FILE) as f:
        file_data = f.read()
    new_version_name = get_new_version_name(file_data)
    new_version_code = get_new_version_code(file_data)

    new_file_data = re.sub(VERSION_NAME_PATTERN, VERSION_NAME_REPLACEMENT.format(new_version_name), file_data)
    new_file_data = re.sub(VERSION_CODE_PATTERN, VERSION_CODE_REPLACEMENT.format(new_version_code), new_file_data)
    new_file_data = re.sub(VERSION_EXAMPLE_PATTERN, VERSION_EXAMPLE_REPLACEMENT.format(new_version_code, new_version_code[2:4], new_version_code[4:6], new_version_code[6:8], new_version_code[8:]), new_file_data)

    with open(GRADLE_FILE, 'w') as f:
        f.write(new_file_data)
    print('pushing new version in branch {0}'.format(BRANCH))
    # os.system('git add {0}'.format(GRADLE_FILE))
    # os.system('git commit -m "Version Bumped to {0}"'.format(new_version_name))
    # os.system('git push origin {0}'.format(BRANCH))

if __name__ == '__main__':
    cli()
