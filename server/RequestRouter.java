package server;

import common.Message;

public class RequestRouter {

    public static Message route(Message request) {

        if (request.type == null) {
            return Message.error("UNKNOWN", request.requestId, "INVALID_REQUEST", "Type missing");
        }

        switch (request.type) {

            case "PING":
                return Message.ok("PING", request.requestId, "pong");

            default:
                return Message.error(request.type, request.requestId, "UNKNOWN_COMMAND", "Command not supported");
        }
    }
}
