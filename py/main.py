import os
import indexconfig
import re
from datetime import datetime

__INDEX_DATA_FILE_PATH__ = '../data/index.data'


def __load_index_config__(config_key: str):
    try:
        return indexconfig.get_index_property_value(config_key)
    except KeyError as ke:
        print(ke)


def __validate_file__(exclude_dot_files: bool, excluded_paths: list, name: str, path: str):
    if exclude_dot_files and re.search("^\.", name):
        return False
    for excluded_path in excluded_paths:
        if excluded_path is not None:
            if re.search(r"^.*" + excluded_path + ".*", path):
                return False
    return True


def __index_path__(path: str, exclude_dot_files: bool, excluded_paths: list, index_file):
    start_time = datetime.now()
    for (dirpath, dirnames, filenames) in os.walk(path):
        for f in filenames:
            if __validate_file__(exclude_dot_files, excluded_paths, f, os.path.join(dirpath, f)):
                index_file.write(os.path.join(dirpath, f)+"\n")
        for d in dirnames:
            if __validate_file__(exclude_dot_files, excluded_paths, d, os.path.join(dirpath, d)):
                index_file.write(os.path.join(dirpath, d)+"\n")
    print(f'Indexing completed for path {path} in {datetime.now() - start_time} time.')


def __create_file_not_exists__(filepath: str):
    if not os.path.exists(filepath):
        print(f'Creating index data file {filepath}')
        fs = open(filepath, 'w+')
        fs.close()
        print(f'Index data file created successfully {filepath}')


def __start_indexing__():
    start_time = datetime.now()
    included_drives: str = __load_index_config__("index.include-drives")
    exclude_dot_files: bool = __load_index_config__("index.exclude-dot-files")
    excluded_paths: list = __load_index_config__("index.excluded-paths")
    print(f'Drives to scan {included_drives}')
    __create_file_not_exists__(__INDEX_DATA_FILE_PATH__);
    with open(__INDEX_DATA_FILE_PATH__, 'w') as index_file:
        for drive in included_drives:
            print(f'Indexing drive {drive}')
            __index_path__(drive, exclude_dot_files, excluded_paths, index_file)
            print(f'Finished Indexing drive {drive}')
    print(f'Total time to index system {datetime.now() - start_time}.')


if __name__ == '__main__':
    __start_indexing__()
