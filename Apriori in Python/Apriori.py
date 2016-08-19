problem_statement = '''

An Efficient Association Rule Hiding Algorithm for Privacy Preserving Data::

The goal of data mining is to extract hidden or useful unknown interesting rules or patterns from databases.
However, the objective of privacy preserving data mining is to hide certain confidential data so that they cannot be
discovered through data mining techniques. In this work, we assume that only sensitive items are given and propose one
algorithm to modify data in database so that sensitive items cannot be deduced through association rules mining algorithms.
More specifically, given a transaction database D, a minimum support, a minimum confidence and a set of items H to be
hidden, the objective is to modify the database D such that no association rules containing H on the right hand side or left hand side will be discovered.

'''

import os,inspect,sys,itertools,re,time,psutil


def open_input_file(file, mode="rt"):
    mod_dir = get_module_dir()
    test_file = os.path.join(mod_dir, file)
    return open(test_file, mode)


def get_module_dir():
    mod_file = inspect.getfile(inspect.currentframe())
    mod_dir = os.path.dirname(mod_file)
    return mod_dir


def percentage_to_integer(percentage):
    percentage = percentage.strip()
    percentage = percentage[:-1]
    return float(percentage)


def get_additional_info():

    additional_info = {}

    with open("assert/additional_info.txt") as source_content:
        for item in source_content:
            variable_raw,value_raw = item.split(":")
            variable = variable_raw.strip()
            value = value_raw.strip()
            additional_info[variable] = value

    try:
        MST_percentage = additional_info["MST"]
        MCT_percentage = additional_info["MCT"]
    except:
        sys.exit("TEXT FILE ERROR [CHECK assert/additional_info.txt FILE]")

    MST = percentage_to_integer(MST_percentage)
    MCT = percentage_to_integer(MCT_percentage)

    source_content.close()
    return MST,MCT


def get_content():

    content = []

    with open("assert/transation_details.txt") as source_content:
        for transation in source_content:
            item_set = re.findall(r"[\w']+", transation.strip())
            content.append(set(item_set))
    source_content.close()
    return content


def count_item_occurence(item,source):
    count = 0

    for sale in source:
        if item.issubset(sale):
            count += 1
    return count


def get_generate_candidates(item_set,frequent_item_sets,k_index):
    next_item_set = []

    for combination_set1,combination_set2 in itertools.combinations(item_set,2):
        combine_combination = combination_set1 | combination_set2
        if len(combine_combination)==k_index+1:
            is_valid = 1
            for subset in itertools.combinations(combine_combination,k_index):
                subset = {tuple(sorted(subset))}
                if not subset.issubset(frequent_item_sets):
                    is_valid = 0
                    break
            if is_valid:
                next_item_set.append(combine_combination)
    return next_item_set


def get_frequent_item_sets(content,item_set,MST):

    frequent_item_sets = set()
    k_index = 1

    while item_set!=[]:
        for item in list(item_set):
            number_of_times_occured = count_item_occurence(item,content)
            if number_of_times_occured<(MST/100)*len(content):
                item_set.remove(item)
        frequent_item_sets = frequent_item_sets | set(map(tuple,map(sorted,item_set)))
        item_set = get_generate_candidates(item_set,frequent_item_sets,k_index)
        k_index += 1
    return map(set,frequent_item_sets)


def get_deriving_association(content,frequent_item_sets,MCT):

    deriving_association = []
    
    for item in frequent_item_sets:
        for r in xrange(1,len(item)):
            for combination in itertools.combinations(item,r):
                numerator,denominator = 0.0,0.0
                subset = set(combination)
                for transation in content:
                    if subset.issubset(transation):
                        denominator += 1
                        if (item-subset).issubset(transation):
                            numerator += 1
                try:
                    if (numerator/denominator)*100>=MCT:
                        deriving_association.append([subset,item-subset,(numerator/denominator)*100])
                except ZeroDivisionError:
                    pass
    return deriving_association


def send_data_to_output_file(deriving_association):

    target_file = open_input_file("output/deriving_association.txt","w")
    for item in deriving_association:
        target_file.write("{0} --> {1} ::: Confidence = {2}\n".format(list(item[0]),list(item[1]),item[2]))
    target_file.close()


def trash_input(deriving_association):

    if deriving_association!=[]:
        final_set = reduce(lambda x,y:set(x)|set(y),zip(*deriving_association)[1])
    else:
        final_set = set()
    source = open_input_file("assert/transation_details.txt")
    content = source.read()
    source.close()
    for character in list(final_set):
        content = content.replace(character+",",'')
    source = open_input_file("assert/transation_details.txt","w")
    source.write(content)
    source.close()

def memory_usage():
    import os
    import psutil
    process = psutil.Process(os.getpid())
    return process.memory_info().rss

if __name__ == "__main__":

    def main_function():
        try:
            import datetime

            start = datetime.datetime.now()

            MST,MCT = get_additional_info()

            content = get_content()
            item_set = [{x} for x in reduce(lambda x,y:x|y,content)]
            total_number_of_transations = len(content)

            frequent_item_sets = get_frequent_item_sets(content,item_set,MST)
            index = max([len(item) for item in frequent_item_sets])

            deriving_association = get_deriving_association(content,filter(lambda x:len(x)==index,frequent_item_sets),MCT)
            send_data_to_output_file(deriving_association)

            trash_input(deriving_association)

            end = datetime.datetime.now()

            print "Succussfully executed"
            print "Execution Time::",(end-start).microseconds*1000," MS"
            print "Memory Usage::",memory_usage()," Bytes"

        except:
            sys.exit("[Some Error Occured]")

    main_function()