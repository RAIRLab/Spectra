from py4j.java_gateway import JavaGateway

gateway = None

def start():
    global gateway
    if not (gateway):
        gateway = JavaGateway()

def stop():
    global gateway
    gateway = None

def plan_from_description(description):
    global gateway
    if not gateway:
        start()
    return gateway.proveFromDescription(description)

def prove(assumptions, goal):

    global gateway
    if not gateway:
        start()

    lst = gateway.newEmptyList()
    for assumption in assumptions:
        lst.append(assumption)

    return gateway.prove(lst, goal)