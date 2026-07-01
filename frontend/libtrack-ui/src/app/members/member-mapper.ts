import {Member} from './member';

export function formToMember(formValue: any): Partial<Member>{
  const member: Partial<Member> = {}

  member.memberNo = Number(formValue.memberNo);

  member.fullName = formValue.fullName;
  member.email = formValue.email;
  member.isActive = formValue.isActive;

  return member;
}

export function formToPartialMember(formValue: any): Partial<Member> {
  const member: Partial<Member> = {};

  if (formValue.memberNo !== '' && formValue.memberNo !== null)
    member.memberNo = Number(formValue.memberNo);

  if (formValue.fullName) member.fullName = formValue.fullName;
  if (formValue.email)    member.email    = formValue.email;

  if (formValue.isActive !== null && formValue.isActive !== undefined)
    member.isActive = formValue.isActive;

  return member;
}
