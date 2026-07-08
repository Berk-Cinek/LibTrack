import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { filter, map, take } from 'rxjs';
import { AuthService } from '../auth-service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return toObservable(authService.checked).pipe(
    filter(Boolean),
    take(1),
    map(() => {
      if (authService.isLoggedIn()) return true;
      authService.authNotice.set('Please log in to view that page.');
      return router.parseUrl('/');
    })
  );
};
