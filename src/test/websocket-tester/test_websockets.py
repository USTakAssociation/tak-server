import websocket as w
import re

WS_URL="ws://localhost:9999"

# Basic logged-out tests that don't require any login or tracking state

def new_conn():
    ws = w.create_connection(WS_URL)
    assert ws.recv() == b"Welcome!\n"
    assert ws.recv() == b"Login or Register\n"
    return ws


def test_server_up():
    ws = new_conn()
    ws.close()


def test_ping():
    ws = new_conn()

    ws.send("PING")
    assert ws.recv() == b"OK\n"
    
    ws.close()


def test_client():
    ws = new_conn()

    ws.send("Client anything-1234")
    assert ws.recv() == b"OK\n"

    ws.send("Client TreffnonX-08.09.16")
    assert ws.recv() == b"Shout <Server> Your Playtak client is unfortunately no longer compatible. Please go to https://www.playtak.com in order to play.\n"

    ws.close()


def test_login_nonexistant_player():
    ws = new_conn()
    ws.send("Login player1 fakepass")
    assert ws.recv() == b"Authentication failure\n"
    ws.close()


def test_login_guest():
    ws = new_conn()
    ws.send("Login Guest")
    assert re.match(r"Welcome Guest[0-9]+!", ws.recv().decode("utf-8"))
    ws.close()


def test_accept_seek_not_logged_in():
    ws = new_conn()
    ws.send("Accept 1234")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_change_password_not_logged_in():
    ws = new_conn()
    ws.send("ChangePassword old new")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_draw_not_logged_in():
    ws = new_conn()
    ws.send("Game#1234 OfferDraw")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_game_list_not_logged_in():
    ws = new_conn()
    ws.send("GameList")
    assert ws.recv() == b"NOK\n"
    ws.close()


def test_full_game_state_not_logged_in():
    ws = new_conn()
    ws.send("Game#1234 Show")
    assert ws.recv() == b"NOK\n"
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

