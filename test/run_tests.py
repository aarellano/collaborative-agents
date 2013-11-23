#!/usr/bin/python
import os

config_file_prefix = "res/config/config"
logs_path = "../res/logs/perf/"

#number of random runs for each test
N = 3

#algorithms to test
algorithms = ["CFS"]
collaborative = ["ORIG"]

#number of agents to use
number_agents = [2,3,4]


def run_particular_test(path, config_fname):
	orig_dir = os.getcwd()
	os.chdir(path)
	os.system("java -XstartOnFirstThread -jar MultiAgentCoverage.jar " + config_fname + ">/dev/null")
	os.chdir(orig_dir)


for algorithm in algorithms:
	for coll in collaborative:
		for agent in number_agents:
			for i in range(N):
				config_fname = config_file_prefix + "_" + algorithm + "_" + coll + "_" + str(agent) + ".xml"
				print "Running test for ", config_fname
				run_particular_test("..", config_fname)

