import { Moment } from 'moment';
import { HiringStatus } from 'app/shared/model/enumerations/hiring-status.model';

export interface IEmployee {
  id?: number;
  name?: string;
  dateOfBirth?: Moment;
  status?: HiringStatus;
  phoneNO?: string;
}

export class Employee implements IEmployee {
  constructor(
    public id?: number,
    public name?: string,
    public dateOfBirth?: Moment,
    public status?: HiringStatus,
    public phoneNO?: string
  ) {}
}
