from pyspark import SQLContext, SparkConf, SparkContext
from pyspark.sql import Row
import argparse
import sys


JARO_THRESH = 0.95


def to_joinable_on_id(row):
    return (row.id_number, row)


def get_unjoined_births(row):
    if row[1][0] is None:
        return True

    return False


def get_unjoined_deaths(row):
    if row[1][1] is None:
        return True

    return False


def get_unjoined_all(row):
    return get_unjoined_births(row) or get_unjoined_deaths(row)


def remove_unjoined_all(row):
    return not get_unjoined_all(row)


def jaro_match(tup):
    import jellyfish

    birth = tup[0]
    death = tup[1]

    birth_name = birth.first_name + ' ' + birth.last_name
    death_name = death.first_name + ' ' + death.last_name

    if jellyfish.jaro_winkler(birth_name, death_name) > JARO_THRESH:
        return True

    return False


def to_jaro_matching_input(tup):
    record = None

    if tup[1][0] is not None:
        tmp = tup[1][0]
        record = Row(
                id_number = tmp.id_number,
                first_name = tmp.first_name,
                last_name = tmp.last_name,
                birth_date = tmp.date
                )

    else:
        tmp = tup[1][1]
        record = Row(
                id_number = tmp.id_number,
                first_name = tmp.first_name,
                last_name = tmp.last_name,
                death_date = tmp.date
                )

    return record


def cart_to_joined_format(tup):
    """ transforms (birth, death) into a useable format """
    bd = ''
    dd = ''

    try:
        bd = tup[0].birth_date
    except AttributeError:
        bd = tup[1].birth_date

    try:
        dd = tup[1].death_date
    except AttributeError:
        dd = tup[0].death_date

    return Row(
            id_number = tup[0].id_number,
            first_name = tup[0].first_name,
            last_name = tup[0].last_name,
            birth_date = bd,
            death_date = dd
            )


def to_joined_format(tup):
    """ transforms (id, (birth, death)) into a useable format """
    return Row(
            id_number = tup[0],
            first_name = tup[1][0].first_name,
            last_name = tup[1][0].last_name,
            birth_date = tup[1][0].date,
            death_date = tup[1][1].date
            )


parser = argparse.ArgumentParser(description='birth and death correlator')
parser.add_argument('births')
parser.add_argument('deaths')
parser.add_argument('output')
args = parser.parse_args()

conf = SparkConf().setAppName("correlate")
sc = SparkContext(conf=conf)
sql = SQLContext(sc)

births_raw = sql.read.load(args.births).rdd
deaths_raw = sql.read.load(args.deaths).rdd

births = births_raw.map(to_joinable_on_id)
deaths = deaths_raw.map(to_joinable_on_id)

both = births.fullOuterJoin(deaths)
unjoined_births = both.filter(get_unjoined_births)
unjoined_deaths = both.filter(get_unjoined_deaths)
correctly_joined = both.filter(remove_unjoined_all).map(to_joined_format)

# do a join with jaro-winkler
jaro_input_births = unjoined_births.map(to_jaro_matching_input)
jaro_input_deaths = unjoined_deaths.map(to_jaro_matching_input)
jaro_input_all = jaro_input_births.cartesian(jaro_input_deaths)
jaro_joined = jaro_input_all.filter(jaro_match).map(cart_to_joined_format)

to_save = sql.createDataFrame(correctly_joined)
to_save.write.save(args.output + '/joined', format="parquet")

to_save = sql.createDataFrame(jaro_joined)
to_save.write.save(args.output + '/jaro_joined', format="parquet")
