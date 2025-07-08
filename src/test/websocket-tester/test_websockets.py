import websocket as w
import re

WS_URL="ws://localhost:9999"


def new_conn():
    ws = w.create_connection(WS_URL)
    assert ws.recv() == b"Welcome!\n"
    assert ws.recv() == b"Login or Register\n"
    return ws

def filtered_recv(ws, allowlist=None):
    filter_prefix = [
        "Online",
        "Seek",
        "GameList",
    ]

    if allowlist:
        filter_prefix = list(set(filter_prefix) - set(allowlist))

    while True:
        message = ws.recv()
        if any(message.decode("utf-8").startswith(prefix) for prefix in filter_prefix):
            continue
        return message


def logged_in_guest_conn():
    ws = new_conn()
    ws.send("Login Guest")

    match = re.match(r"Welcome (Guest[0-9]+)!", filtered_recv(ws).decode("utf-8"))
    assert match

    return ws, match.group(1)


def create_seek_v1(ws):
    ws.send("Seek 5 15 10 W")
    r = filtered_recv(ws, ["Seek"])
    match = re.match(r"^Seek new ([0-9]+)", r.decode("utf-8"))
    assert match
    return match.group(1)


def test_server_up():
    ws = new_conn()
    ws.close()


def test_ping():
    ws = new_conn()

    ws.send("PING")
    assert filtered_recv(ws) == b"OK\n"
    
    ws.close()


def test_client():
    ws = new_conn()

    ws.send("Client anything-1234")
    assert filtered_recv(ws) == b"OK\n"

    ws.send("Client TreffnonX-08.09.16")
    assert filtered_recv(ws) == b"Shout <Server> Your Playtak client is unfortunately no longer compatible. Please go to https://www.playtak.com in order to play.\n"

    ws.close()


def test_two_guests_create_play_game():
    guest1conn, guest1name = logged_in_guest_conn()
    guest2conn, guest2name = logged_in_guest_conn()

    seeknum = create_seek_v1(guest1conn)
    guest2conn.send(f"Accept {seeknum}")

    g2resp = filtered_recv(guest2conn)
    g2match = re.match(f"Game Start ([0-9]+) 5 {guest1name} vs {guest2name} black 15 0 21 1 0 0\n", g2resp.decode("utf-8"))
    assert g2match

    g1resp = filtered_recv(guest1conn)
    g1match = re.match(f"Game Start ([0-9]+) 5 {guest1name} vs {guest2name} white 15 0 21 1 0 0\n", g1resp.decode("utf-8"))
    assert g1match

    g2gamenum = g2match.group(1)
    g1gamenum = g1match.group(1)

    assert g2gamenum == g1gamenum

    timerex = f"Game#{g1gamenum} Time [0-9]+ [0-9]+\n"

    guest1conn.send(f"Game#{g1gamenum} P A1")

    g1resp = filtered_recv(guest1conn)
    assert re.match(timerex, g1resp.decode("utf-8"))

    g2resp = filtered_recv(guest2conn)
    assert re.match(timerex, g2resp.decode("utf-8"))

    g2resp = filtered_recv(guest2conn)
    assert g2resp.decode("utf-8") == f"Game#{g1gamenum} P A1\n"
    
    g1resp = filtered_recv(guest1conn)
    assert g1resp == b"OK\n"

    guest2conn.send(f"Game#{g1gamenum} P A2")

    g2resp = filtered_recv(guest2conn)
    assert re.match(timerex, g2resp.decode("utf-8"))

    g1resp = filtered_recv(guest1conn)
    assert re.match(timerex, g1resp.decode("utf-8"))

    g1resp = filtered_recv(guest1conn)
    assert g1resp.decode("utf-8") == f"Game#{g1gamenum} P A2\n"
    
    g2resp = filtered_recv(guest2conn)
    assert g2resp == b"OK\n"


    guest1conn.send(f"Game#{g1gamenum} M A2 A1 1")

    g1resp = filtered_recv(guest1conn)
    assert re.match(timerex, g1resp.decode("utf-8"))

    g2resp = filtered_recv(guest2conn)
    assert re.match(timerex, g2resp.decode("utf-8"))

    g2resp = filtered_recv(guest2conn)
    assert g2resp.decode("utf-8") == f"Game#{g1gamenum} M A2 A1 1\n"
    
    g1resp = filtered_recv(guest1conn)
    assert g1resp == b"OK\n"


    guest2conn.send(f"Game#{g1gamenum} Resign")
    g2resp = filtered_recv(guest2conn)
    assert g2resp.decode("utf-8") == f"Game#{g1gamenum} Over 1-0\n"

    g1resp = filtered_recv(guest1conn)
    assert g1resp.decode("utf-8") == f"Game#{g1gamenum} Over 1-0\n"

    guest1conn.close()
    guest2conn.close()


def test_login_nonexistant_player():
    ws = new_conn()
    ws.send("Login player1 fakepass")
    assert filtered_recv(ws) == b"Authentication failure\n"
    ws.close()


def test_login_guest():
    ws = new_conn()
    ws.send("Login Guest")
    assert re.match(r"Welcome Guest[0-9]+!", filtered_recv(ws).decode("utf-8"))
    ws.close()


def test_accept_seek_not_logged_in():
    ws = new_conn()
    ws.send("Accept 1234")
    assert filtered_recv(ws) == b"NOK\n"
    ws.close()


def test_change_password_not_logged_in():
    ws = new_conn()
    ws.send("ChangePassword old new")
    assert filtered_recv(ws) == b"NOK\n"
    ws.close()


def test_draw_not_logged_in():
    ws = new_conn()
    ws.send("Game#1234 OfferDraw")
    assert filtered_recv(ws) == b"NOK\n"
    ws.close()


def test_game_list_not_logged_in():
    ws = new_conn()
    ws.send("GameList")
    assert filtered_recv(ws) == b"NOK\n"
    ws.close()


def test_full_game_state_not_logged_in():
    ws = new_conn()
    ws.send("Game#1234 Show")
    assert filtered_recv(ws) == b"NOK\n"
    ws.close()


def test_square_state_not_logged_in():
    ws = new_conn()
    ws.send("Game#1234 Show A1")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_join_room_not_logged_in():
    ws = new_conn()
    ws.send("JoinRoom 1234")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_leave_room_not_logged_in():
    ws = new_conn()
    ws.send("LeaveRoom 1234")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_list_all_seeks_not_logged_in():
    ws = new_conn()
    ws.send("List")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_move_not_logged_in():
    ws = new_conn()
    ws.send("Game#1234 M A1 A2")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_observe_not_logged_in():
    ws = new_conn()
    ws.send("Observe 1234")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_place_not_logged_in():
    ws = new_conn()
    ws.send("Game#1234 P A1")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_registration():
    ws = new_conn()
    ws.send("Register someplayer some@example.com")
    assert ws.recv() == b"Registered someplayer. Check your email for password\n"
    ws.close()


def test_remove_draw_not_logged_in():
    ws = new_conn()
    ws.send("Game#1234 RemoveDraw")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_remove_undo_not_logged_in():
    ws = new_conn()
    ws.send("Game#1234 RemoveUndo")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_reset_password():
    ws = new_conn()
    ws.send("ResetPassword notaplayer notatoken newpass")
    assert ws.recv() == b"No such player\n"
    ws.close()


def test_resign_not_logged_in():
    ws = new_conn()
    ws.send("Game#1234 Resign")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_seek_v3_not_logged_in():
    ws = new_conn()
    ws.send("Seek 5 15 10 A 2 21 1 0 0 0 0 anyopponent")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_seek_v2_not_logged_in():
    ws = new_conn()
    ws.send("Seek 5 15 10 A 2 21 1 0 0 anyopponent")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_seek_v1_not_logged_in():
    ws = new_conn()
    ws.send("Seek 5 15 10 W")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_send_reset_token_player_missing():
    ws = new_conn()
    ws.send("SendResetToken notaplayer notaplayer@example.com")
    assert ws.recv() == b"Reset Token Error: No such player\n"
    ws.close()


def test_shout_not_logged_in():
    ws = new_conn()
    ws.send("Shout waaaaah")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_shout_room_not_logged_in():
    ws = new_conn()
    ws.send("ShoutRoom 1234 eanrseitnaoes")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_sudo_ban_not_logged_in():
    ws = new_conn()
    ws.send("sudo ban someplayer123")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_sudo_broadcast_not_logged_in():
    ws = new_conn()
    ws.send("sudo broadcast someplayer123")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_sudo_gag_not_logged_in():
    ws = new_conn()
    ws.send("sudo gag someplayer123")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_sudo_kick_not_logged_in():
    ws = new_conn()
    ws.send("sudo kick someplayer123")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_sudo_list_not_logged_in():
    ws = new_conn()
    ws.send("sudo list mods")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_sudo_mod_not_logged_in():
    ws = new_conn()
    ws.send("sudo mod someplayer123")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_sudo_reload_not_logged_in():
    ws = new_conn()
    ws.send("sudo reload wordconfig")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_sudo_set_not_logged_in():
    ws = new_conn()
    ws.send("sudo set password someuser123 value")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_sudo_unban_not_logged_in():
    ws = new_conn()
    ws.send("sudo unban player123")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_sudo_ungag_not_logged_in():
    ws = new_conn()
    ws.send("sudo ungag player123")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_sudo_unmod_not_logged_in():
    ws = new_conn()
    ws.send("sudo unmod player123")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_tell_not_logged_in():
    ws = new_conn()
    ws.send("tell player123 something")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_undo_not_logged_in():
    ws = new_conn()
    ws.send("Game#1234 Undo")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_unobserve_not_logged_in():
    ws = new_conn()
    ws.send("unobserve 1234")
    assert ws.recv() == b"NOK\n"
    ws.close()

