/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pockethub.ui.user;

import static com.github.pockethub.Intents.EXTRA_USER;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.pockethub.R;
import com.github.pockethub.ThrowableLoader;
import com.github.pockethub.accounts.AccountUtils;
import com.github.pockethub.ui.ItemListFragment;
import com.github.pockethub.util.AvatarLoader;
import com.google.inject.Inject;

import java.util.List;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.OrganizationService;

/**
 * Fragment to display the members of an org.
 */
public class MembersFragment extends ItemListFragment<User> {

    private User org;

    @Inject
    private OrganizationService service;

    @Inject
    private AvatarLoader avatars;

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (org != null)
            outState.putSerializable(EXTRA_USER, org);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        org = (User) getArguments().getSerializable("org");
        if (org == null && savedInstanceState != null)
            org = (User) savedInstanceState.getSerializable(EXTRA_USER);
        setEmptyText(R.string.no_members);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new ThrowableLoader<List<User>>(getActivity(), items) {

            @Override
            public List<User> loadData() throws Exception {
                return service.getMembers(org.getLogin());
            }
        };
    }

    @Override
    protected SingleTypeAdapter<User> createAdapter(List<User> items) {
        User[] users = items.toArray(new User[items.size()]);
        return new UserListAdapter(getActivity().getLayoutInflater(), users,
                avatars);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        User user = (User) l.getItemAtPosition(position);
        if (!AccountUtils.isUser(getActivity(), user))
            startActivity(UserViewActivity.createIntent(user));
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_members_load;
    }
}
