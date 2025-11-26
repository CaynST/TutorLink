#!/usr/bin/env python3
from http.server import BaseHTTPRequestHandler, HTTPServer
import json

class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        if self.path == '/api/generate':
            length = int(self.headers.get('content-length', 0))
            body = self.rfile.read(length).decode('utf-8') if length else ''
            try:
                _ = json.loads(body) if body else {}
            except Exception:
                pass
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            resp = {
                'response': 'Respuesta simulada por el mock de Ollama para pruebas locales.'
            }
            self.wfile.write(json.dumps(resp).encode('utf-8'))
        else:
            self.send_response(404)
            self.end_headers()

    def log_message(self, format, *args):
        # Quiet logging to stdout to keep output clean
        print(format % args)

if __name__ == '__main__':
    server = HTTPServer(('0.0.0.0', 11434), Handler)
    print('Mock Ollama listening on :11434')
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print('\nStopping mock Ollama')
        server.server_close()
