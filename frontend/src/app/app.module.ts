import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration, withEventReplay } from '@angular/platform-browser';

import { providePrimeNG } from 'primeng/config';
import Material from '@primeng/themes/material';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ButtonModule } from 'primeng/button';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ButtonModule
  ],
  providers: [
    provideClientHydration(withEventReplay()),
    providePrimeNG({
      theme: {
          preset: Material,
          options: {
            darkModeSelector: false || 'none'
        }
      }
  })
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
