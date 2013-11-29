#!/usr/bin/python
import os
import sys
import subprocess
import pickle

config_file_prefix = "res/config/config"
logs_path = "../res/logs/perf/"

#algorithms to test
maps = ["empty","reg","rep","rooms"]
maps_description = {}
algorithms = ["CFS","GS","DGS"]
collaborative = ["GAUSS", "GAUSS2","ORIG","GAUSS", "FOLLOWERS_BRAKER", "SHARED_PLAN"]
sameStartingPosition = ["true","false"]

#number of agents to use
number_agents = [1,2,3,4,5,10]


def run_defined_test(path, config_fname):
	orig_dir = os.getcwd()
	os.chdir(path)
	subprocess.call("java -XstartOnFirstThread -jar MultiAgentCoverage.jar " + config_fname + ">/dev/null", shell=True)
	os.chdir(orig_dir)


def initialize_hash():
	res = {}
	for coll in collaborative:
		res[coll] = {}
		for algorithm in algorithms:
			res[coll][algorithm] = {}
			for mapa in maps:
				res[coll][algorithm][mapa] = {}
				for pos in sameStartingPosition:
					res[coll][algorithm][mapa][pos] = {}
					for agents in number_agents:
						res[coll][algorithm][mapa][pos][str(agents)] = {}
						for data in ['revisited','steps','time','obstacles','cells']:
							res[coll][algorithm][mapa][pos][str(agents)][data] = []
	return res

def parse_log_files(logs_path):
	res = initialize_hash();
	for log_fname in os.listdir(logs_path):
		split = log_fname.split('_')
		mapa = split[0]
		agents = split[1]
		algorithm = split[2]
		collaborative = split[3]
		counter = 4
		if (collaborative == "SHARED"):
			collaborative = "SHARED_PLAN"
			counter = 5
		if (collaborative == "FOLLOWERS"):
			collaborative = "FOLLOWERS_BRAKER"
			counter = 5
		position = split[counter]
		if (position == "random"):
			position = "false"
		if (position == "fixed"):
			position = "true"
		log_fname = logs_path + '/' + log_fname
		fd = open(log_fname)
		values = []
		for line in fd.readlines():
			values.append(line[line.find(': ')+2:].strip())
		fd.close()
		res[collaborative][algorithm][mapa][position][agents]['revisited'].append(int(values[0]))
		res[collaborative][algorithm][mapa][position][agents]['steps'].append(int(values[1]))
		res[collaborative][algorithm][mapa][position][agents]['time'].append(int(values[2]))
		res[collaborative][algorithm][mapa][position][agents]['obstacles'].append(int(values[3]))
		res[collaborative][algorithm][mapa][position][agents]['cells'].append(int(values[4]))
		if not maps_description.has_key(mapa):
			maps_description[mapa] = {}
			maps_description[mapa]['obstacles'] = int(values[3])
			maps_description[mapa]['cells'] = int(values[4])
		
	return res

def hash_to_csv(hashtbl, fname):
	separator = ','
	fd = open(fname,'w')
	fd.write("Map" + separator + "Map obstacles ratio" + separator + "Map num obstacles" + separator + "Map num cells" + separator + 
				"Initial Position" + separator + "Algorithm" + separator + "Coll type" + separator + "Number of Agents" + separator)
	fd.write("Initial Pos" + separator + "Number of revisited cells" + separator + "Number of steps" + separator + "Time" + "\n")
	for coll in sorted(hashtbl):
		for algorithm in sorted(hashtbl[coll]):
			for mapa in sorted(hashtbl[coll][algorithm]):
				for pos in sorted(hashtbl[coll][algorithm][mapa]):
					for agent in sorted(hashtbl[coll][algorithm][mapa][pos]):
						for i in range(0,len(hashtbl[coll][algorithm][mapa][pos][agent]['revisited'])):
							fd.write(mapa + separator)
							fd.write(str(float(maps_description[mapa]['obstacles'])/float(maps_description[mapa]['cells'])) + separator)
							fd.write(str(maps_description[mapa]['obstacles']) + separator)
							fd.write(str(maps_description[mapa]['cells']) + separator)
							fd.write(coll + separator)
							fd.write(algorithm + separator)
							fd.write(agent + separator)	
							fd.write("random" + separator)	#initial position is always random
							fd.write(str(hashtbl[coll][algorithm][mapa][pos][agent]['revisited'][i]) + separator)
							fd.write(str(hashtbl[coll][algorithm][mapa][pos][agent]['steps'][i]) + separator)
							fd.write(str(hashtbl[coll][algorithm][mapa][pos][agent]['time'][i]) + separator)
							fd.write('\n')
	fd.close()



if __name__ == "__main__":
	if len(sys.argv) != 5:
	    sys.stderr.write('Usage: sys.argv[0] logs_paths results_fname result_serialization_fname number_tests_to_run\n')
	    sys.exit(1)
	logs_folder_path = sys.argv[1]
	results_fname = sys.argv[2]
	result_serialization_fname = sys.argv[3]
	N = int(sys.argv[4])

	for mapa in maps:
		for algorithm in algorithms:
			for coll in collaborative:
				for pos in sameStartingPosition:
					if pos == "true":
						str_pos = "fixed"
					else:
						str_pos = "random"
					for agent in number_agents:
						for i in range(N):
							config_fname = config_file_prefix + "_" + mapa + "_" + algorithm + "_" + coll + "_" + str_pos + "_" + str(agent) + ".xml"
							print "Running test " + str(i+1) + " out of " + str(N) + " for " + config_fname
							run_defined_test("..", config_fname)

	res = parse_log_files(logs_folder_path)
	pickle.dump(res, open(result_serialization_fname, "wb" ))
	hash_to_csv(res, results_fname)
