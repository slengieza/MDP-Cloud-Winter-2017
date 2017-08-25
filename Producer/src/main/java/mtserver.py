import socket
import xml.etree.ElementTree
HOST, PORT = ' ',3001


listen_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
listen_socket.setsockopt(socket.SOL_SOCKET, socket.SOREUSEADDR,1)
listen_socket.bind((HOST,PORT))
listen_socket.listen(1)

tree = ElementTree.ElementTree(file='C:\Rockwell Automation\WorkingDirectory\MTConnect\devices.xml')
xmlstr = ElementTree.tostring(et, encoding='utf8', method='xml')

while True:
	client_connection,client_address = listen_socket.accept()
	request = client_connection.recv(1024)
	prin (request)
	
	http_respone = xmlstr
	
	client_connection.sendall(bytes(http_response.encode('utf-8')))
	client_connection.close()