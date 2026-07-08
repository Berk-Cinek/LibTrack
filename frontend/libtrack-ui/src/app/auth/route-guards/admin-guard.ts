import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { filter, map, take } from 'rxjs';
import { AuthService } from '../auth-service';

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return toObservable(authService.checked).pipe(
    filter(checked => checked),
    take(1),
    map( () => authService.isAdmin() ? true : router.parseUrl('/'))
  );
};
