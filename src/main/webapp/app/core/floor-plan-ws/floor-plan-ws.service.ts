import { Injectable, inject } from '@angular/core';
import { Location } from '@angular/common';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import SockJS from 'sockjs-client';
import { RxStomp } from '@stomp/rx-stomp';
import { AuthServerProvider } from 'app/core/auth/auth-jwt.service';

@Injectable({ providedIn: 'root' })
export class FloorPlanWsService {
  private readonly rxStomp = new RxStomp();
  private readonly authServerProvider = inject(AuthServerProvider);
  private readonly location = inject(Location);

  private connected = false;

  connect(): void {
    if (this.connected) return;
    this.connected = true;
    const authToken = this.authServerProvider.getToken();
    let url = '/websocket/tracker';
    url = this.location.prepareExternalUrl(url);
    if (authToken) url = `${url}?access_token=${authToken}`;
    this.rxStomp.configure({
      webSocketFactory: () => new SockJS(url),
      reconnectDelay: 5000,
    });
    this.rxStomp.activate();
  }

  watchLocation(locationId: number): Observable<void> {
    this.connect();
    return this.rxStomp.watch(`/topic/floor-plan/${locationId}`).pipe(map(() => undefined));
  }

  disconnect(): Promise<void> {
    this.connected = false;
    return this.rxStomp.deactivate();
  }
}
