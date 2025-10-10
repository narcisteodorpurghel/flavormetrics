import { NgModule } from '@angular/core';
import { HOME_SERVICE_TOKEN } from './di/custom-injections';
import { HomeServiceImpl } from './pages/(home)/services/home/home.service.impl';

@NgModule({
  providers: [{ provide: HOME_SERVICE_TOKEN, useClass: HomeServiceImpl }],
})
export class AppModule {}
