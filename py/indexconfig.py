import yaml
import os

__INDEXER_CONFIG_PATH__ = "./config/index-config.yaml"


def __file_exists__(filepath: str, filetype: str):
    if not os.path.exists(filepath):
        raise FileNotFoundError(fr'{filetype} file not found at path {filepath}.')
    return True


def __read_file__(filepath: str):
    with open(filepath) as file:
        print(fr'Loading file ${filepath}')
        data = yaml.load(file, Loader=yaml.FullLoader)
        print(fr'File ${filepath} loaded successfully')
        return data


def __read_indexer_config__():
    try:
        if __file_exists__(__INDEXER_CONFIG_PATH__, "Index config"):
            return __read_file__(__INDEXER_CONFIG_PATH__)
    except FileNotFoundError as fe:
        print(fe)


def get_index_property_value(key: str):
    data = __read_indexer_config__()
    __keys = key.split(".")
    for __key in __keys:
        if not data.__contains__(__key):
            raise KeyError(fr'Invalid key-part {__key} found in {key}')
        data = data.get(__key)
    return data
