import { Provider } from '@angular/core';
import { Cloudinary } from '@cloudinary/url-gen';
import { environment } from '../../../environments/environment';

export abstract class CloudinaryService {
  abstract getCloudinary(): Cloudinary;
}

export class CloudinaryServiceImpl implements CloudinaryService {
  static readonly CLOUDINARY = new Cloudinary({
    cloud: {
      cloudName: 'ddmamrq9r',
      apiKey: environment.cloudinary.apiKey,
      apiSecret: environment.cloudinary.apiSecret,
    },
  });

  getCloudinary() {
    return CloudinaryServiceImpl.CLOUDINARY;
  }
}

export function provideCloudinaryService(): Provider {
  return {
    provide: CloudinaryService,
    useClass: CloudinaryServiceImpl,
  };
}
