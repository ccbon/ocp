#!/usr/bin/ksh
set -eau

ROOT_DIR=./world
DHT_EXE=/cygdrive/c/jlouis/sandbox/test/test_dht/test_dht.exe

create_agent() {
	typeset agent_nbr="${1}"
	echo "creating agent $1"
	typeset agent_dir="${ROOT_DIR}/agent_${1}"
	mkdir -p "${agent_dir}"
	typeset agent_ping=''
	if [ "${agent_nbr}" != 0 ]; then
		agent_ping='agent_2=localhost:22220'
	else
		agent_ping='first_agent=yes'
	fi
	cat > "${agent_dir}/dht.properties" <<EOF
# properties file for DHT
# file specify a port for the DHT listener
# and the list of other agent it can contact when initializing
is_listening=yes
tcp_listener_port=2222${agent_nbr}
udp_listener_port=3333${agent_nbr}
default.key_length=8
agent_1=localhost:11111
${agent_ping}
EOF
	ln -s "${DHT_EXE}" "${agent_dir}/agent_${agent_nbr}.exe"
	
}

set +e
(
set -e

# create the file system with N agent
integer N=5
integer i=0

rm -rf "${ROOT_DIR}"

while ((i<N))
do
	echo "i=$i"
	create_agent $i
	((i=i+1))
done

# start the first agent
cd "${ROOT_DIR}/agent_0"
./agent_0

)
if [ $? != 0 ]; then echo "Error..."; fi
