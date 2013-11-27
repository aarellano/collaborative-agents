#!/usr/bin/python
import os
import sys
import subprocess
import pickle
import numpy


#algorithms to test
maps = ["empty"]#,"reg","rep","spiders"]
algorithms = ["CFS"]
collaborative = ["ORIG"]#,"GAUSS"]
number_agents = [1,2,3]
#TODO: not implemented yet
initial_pos = ["random"]


def load_hash(fname):
	return pickle.load(open(fname))


def initialize_hash():
	res = {}
	for coll in collaborative:
		res[coll] = {}
		for algorithm in algorithms:
			res[coll][algorithm] = {}
			for mapa in maps:
				res[coll][algorithm][mapa] = {}
				for agents in number_agents:
					res[coll][algorithm][mapa][str(agents)] = {}
					for data in ['avg revisited','std dev revisited','avg time','std dev time','obstacles','cells']:
						res[coll][algorithm][mapa][str(agents)][data] = []
	return res



def filter_hash(hashtbl):
	res = initialize_hash();
	for coll in collaborative:
		for algorithm in algorithms:
			for mapa in maps:
				for agent in number_agents:
					agent = str(agent)
					res[coll][algorithm][mapa][agent]['obstacles'] = hashtbl[coll][algorithm][mapa][agent]['obstacles']
					res[coll][algorithm][mapa][agent]['cells'] = hashtbl[coll][algorithm][mapa][agent]['cells']
					arr = numpy.array(hashtbl[coll][algorithm][mapa][agent]['revisited'])
					res[coll][algorithm][mapa][agent]['avg revisited'] = numpy.average(arr)
					res[coll][algorithm][mapa][agent]['std dev revisited'] = numpy.std(arr)
					arr = numpy.array(hashtbl[coll][algorithm][mapa][agent]['time'])
					res[coll][algorithm][mapa][agent]['avg time'] = numpy.average(arr)
					res[coll][algorithm][mapa][agent]['std dev time'] = numpy.std(arr)
					# TODO
					# for pos in initial_pos:
					# 	if pos == res[coll][algorithm][mapa][agent][pos]???
	return res
						
	


def hash_to_csv(hashtbl, fname):
	separator = ','
	fd = open(fname,'w')
	fd.write("Map" + separator + "Map obstacles ratio" + separator + "Map num obstacles" + separator + "Map num cells" + separator + 
				"Algorithm" + separator + "Coll type" + separator + "Number of Agents" + separator)
	fd.write("Initial Pos" + separator + "Avg number of revisited cells" + separator + "Std dev revisited cells" + separator + 
				"Avg time" + separator + "Std dev time\n")
	for coll in sorted(hashtbl):
		for algorithm in sorted(hashtbl[coll]):
			for mapa in sorted(hashtbl[coll][algorithm]):
				for agent in sorted(hashtbl[coll][algorithm][mapa]):
					fd.write(mapa + separator)
					fd.write(str(float(hashtbl[coll][algorithm][mapa][agent]['obstacles'][0])/float(hashtbl[coll][algorithm][mapa][agent]['cells'][0])) + separator)
					fd.write(str(hashtbl[coll][algorithm][mapa][agent]['obstacles'][0]) + separator)
					fd.write(str(hashtbl[coll][algorithm][mapa][agent]['cells'][0]) + separator)
					fd.write(coll + separator)
					fd.write(algorithm + separator)
					fd.write(agent + separator)	
					fd.write("random" + separator)	#initial position is always random
					fd.write(str(hashtbl[coll][algorithm][mapa][agent]['avg revisited']) + separator)
					fd.write(str(hashtbl[coll][algorithm][mapa][agent]['std dev revisited']) + separator)
					fd.write(str(hashtbl[coll][algorithm][mapa][agent]['avg time']) + separator)
					fd.write(str(hashtbl[coll][algorithm][mapa][agent]['std dev time']) + separator)
					fd.write('\n')
	fd.close()


if __name__ == "__main__":
	if len(sys.argv) != 3:
	    sys.stderr.write('Usage: sys.argv[0] serial_object_fname output_fname\n')
	    sys.exit(1)

	serial_object_fname = sys.argv[1]
	output_fname = sys.argv[2]

	hashtbl = load_hash(serial_object_fname)
	hashtbl = filter_hash(hashtbl)
	hash_to_csv(hashtbl, output_fname)
